package com.sksamuel.scrimage.scaling

import java.awt.image.Raster

import com.sksamuel.scrimage.Image

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._

object ResampleOpScala {

  private val MAX_WAIT_PER_PASS = 10.minutes

  case class ResampFilter(samplingRadius: Int, kernel: Float => Float) {
    def apply(x: Float): Float = kernel(x)
  }

  def bicubicKernel(a: Float)(x: Float): Float = {
    if (x == 0) 1.0f
    else if (x < 0.0f) bicubicKernel(a)(-x)
    else {
      val xx = x * x
      val xxx = xx * x
      if (x < 1.0f) (a + 2f) * xxx - (a + 3f) * xx + 1f
      else if (x < 2.0f) a * xxx - 5 * a * xx + 8 * a * x - 4 * a
      else 0.0f
    }
  }

  def bilinearKernel(x: Float): Float = {
    if (x >= 0.0f && x < 1.0f) 1.0f - x
    else if (-1.0f < x && x < 0.0f) 1.0f + x
    else 0.0f
  }

  def bSplineKernel(x: Float): Float = {
    if (x < 0.0f) bSplineKernel(-x)
    else if (x < 1.0f) {
      val xx = x * x
      0.5f * xx * x - xx + (2.0f / 3.0f)
    } else if (x < 2.0f) {
      val y = 2.0f - x
      (1.0f / 6.0f) * y * y * y
    } else 0.0f
  }

  private def sinc(x: Float): Float = Math.sin(x).toFloat / x
  private val PIf = Math.PI.toFloat

  def lanczos3Kernel(x: Float): Float = {
    if (x == 0) 1.0f
    else if (-3.0f < x && x < 3.0f) sinc(x * PIf) * sinc(x * PIf / 3.0f)
    else 0.0f
  }

  val bicubicFilter = ResampFilter(2, bicubicKernel(-0.5f))
  val bilinearFilter = ResampFilter(1, bilinearKernel)
  val bSplineFilter = ResampFilter(2, bSplineKernel)
  val lanczos3Filter = ResampFilter(3, lanczos3Kernel)

  case class SubSamplingData(arrN: Array[Int],
                             arrPixel: Array[Int],
                             arrWeight: Array[Float],
                             numContributors: Int)

  private[this] def createSubSampling(filter: ResampFilter, srcSize: Int, dstSize: Int): SubSamplingData = {
    val scale = dstSize.toFloat / srcSize.toFloat
    val arrN = Array.ofDim[Int](dstSize)
    var numContributors: Int = 0
    var arrWeight: Array[Float] = null
    var arrPixel: Array[Int] = null
    val fwidth = filter.samplingRadius
    val centerOffset = 0.5f / scale

    var subindex = 0
    var center = centerOffset
    var left = 0
    var right = 0
    var j = 0
    var weight = 0f
    var tot = 0f
    var k = 0
    var n = 0
    var max = 0
    if (scale < 1.0f) {
      val width = fwidth / scale
      numContributors = (width * 2.0f + 2).toInt
      arrWeight = Array.ofDim[Float](dstSize * numContributors)
      arrPixel = Array.ofDim[Int](dstSize * numContributors)
      val fNormFac = (1f / (Math.ceil(width) / fwidth)).toFloat
      var i = 0
      while (i < dstSize) {
        subindex = i * numContributors
        center = i / scale + centerOffset
        left = Math.floor(center - width).toInt
        right = Math.ceil(center + width).toInt
        j = left
        while (j <= right) {
          weight = filter((center - j) * fNormFac)
          if (weight != 0.0f) {
            n = if (j < 0) -j
            else if (j >= srcSize) srcSize - j + srcSize - 1
            else j
            k = arrN(i)
            arrN(i) += 1
            arrPixel(subindex + k) = n
            arrWeight(subindex + k) = if (n < 0 || n >= srcSize) 0.0f
            else weight
          }
          j += 1
        }
        max = arrN(i)
        tot = 0f
        k = 0
        while (k < max) {
          tot += arrWeight(subindex + k)
          k += 1
        }
        if (tot != 0f) {
          k = 0
          while (k < max) {
            arrWeight(subindex + k) /= tot
            k += 1
          }
        }
        i += 1
      }
    } else {
      numContributors = (fwidth * 2.0f + 1).toInt
      arrWeight = Array.ofDim[Float](dstSize * numContributors)
      arrPixel = Array.ofDim[Int](dstSize * numContributors)
      var i = 0
      while (i < dstSize) {
        subindex = i * numContributors
        center = i / scale + centerOffset
        left = Math.floor(center - fwidth).toInt
        right = Math.ceil(center + fwidth).toInt
        j = left
        while (j <= right) {
          weight = filter(center - j)
          if (weight != 0.0f) {
            val n: Int = if (j < 0) -j
            else if (j >= srcSize) srcSize - j + srcSize - 1
            else j
            k = arrN(i)
            arrN(i) += 1
            if (n < 0 || n >= srcSize) weight = 0.0f
            arrPixel(subindex + k) = n
            arrWeight(subindex + k) = weight
          }
          j += 1
        }
        max = arrN(i)
        tot = 0f
        k = 0
        while (k < max) {
          tot += arrWeight(subindex + k)
          k += 1
        }
        assert(tot != 0) // "should never happen except bug in filter"
        if (tot != 0f) {
          k = 0
          while (k < max) {
            arrWeight(subindex + k) /= tot
            k += 1
          }
        }
        i += 1
      }
    }

    SubSamplingData(arrN, arrPixel, arrWeight, numContributors)
  }

  def scaleTo(filter: ResampFilter)(img: Image)(dstWidth: Int, dstHeight: Int, numberOfThreads: Int = 0): Image = {
    require(dstWidth >= 3 && dstHeight >= 3,
      s"Error doing rescale. Target was $dstWidth x $dstHeight but must be at least 3x3.")

    val srcWidth = img.width
    val srcHeight = img.height

    val hSampling = createSubSampling(filter, srcWidth, dstWidth)
    val vSampling = createSubSampling(filter, srcHeight, dstHeight)

//    val srcRaster = img.raster
    //    val workRaster = srcRaster.empty(dstWidth, srcHeight)
    //    val outRaster = srcRaster.empty(dstWidth, dstHeight)
    //
    //    val horizontals = for ( i <- 0 until numberOfThreads ) yield {
    //      val finalI = i
    //      Future {
    //        blocking {
    //          horizontallyFromSrcToWork(
    //            srcRaster, workRaster,
    //            finalI, numberOfThreads, hSampling)
    //        }
    //      }
    //    }
    //    Await.ready(Future sequence horizontals, MAX_WAIT_PER_PASS)
    //
    //    val verticles = for ( i <- 0 until numberOfThreads ) yield {
    //      val finalI = i
    //      Future {
    //        blocking {
    //          verticalFromWorkToDst(
    //            workRaster, outRaster,
    //            finalI, numberOfThreads, vSampling)
    //        }
    //      }
    //    }
    //    Await.ready(Future sequence verticles, MAX_WAIT_PER_PASS)
    //    new Image(outRaster)

    // todo reimplement
    ???
  }

  private[this] def horizontallyFromSrcToWork(src: Raster,
                                              work: Raster,
                                              start: Int,
                                              delta: Int,
                                              hSampling: SubSamplingData): Unit = {
    var x = 0
    var y = start
    var j = 0
    var index = 0

    //    val pixelSize = src.n_channel * 1
    //
    //    val srcHeight = src.height
    //    val dstWidth = work.width
    //    val n_channel = src.n_channel
    //    var sample = Array.ofDim[Float](n_channel)
    //    var c = 0
    //    var c0 = 0
    //
    //    while (y < srcHeight) {
    //      x = dstWidth - 1
    //      j = 0
    //      while (x >= 0) {
    //        java.util.Arrays.fill(sample, 0.0f)
    //        index = x * hSampling.numContributors
    //        j = hSampling.arrN(x) - 1
    //
    //        while (j >= 0) {
    //          c0 = src.offset(hSampling.arrPixel(index), y)
    //          c = 0
    //          while (c < n_channel) {
    //            sample(c) += (src.readChannel(c0)) * hSampling.arrWeight(index)
    //            c += 1
    //            c0 += 1
    //          }
    //          index += 1
    //          j -= 1
    //        }
    //        c0 = work.offset(x, y)
    //        c = 0
    //        while (c < n_channel) {
    //          work.writeChannel(c0)(fit(sample(c), work.maxChannelValue))
    //          c += 1
    //          c0 += 1
    //        }
    //        x -= 1
    //      }
    //      y = y + delta
    //    }
  }

  private[this] def verticalFromWorkToDst(work: Raster,
                                          out: Raster,
                                          start: Int,
                                          delta: Int,
                                          vSampling: SubSamplingData): Unit = {
    //    var x = start
    //    var y = 0
    //    val dstWidth = work.width
    //    val dstHeight = out.height
    //    val n_channel = work.n_channel
    //    var sample = Array.ofDim[Float](n_channel)
    //    var index = 0
    //    var j = 0
    //    var c = 0
    //    var c0 = 0
    //
    //    while (x < dstWidth) {
    //      y = dstHeight - 1
    //      while (y >= 0) {
    //        java.util.Arrays.fill(sample, 0.0f)
    //        index = y * vSampling.numContributors
    //        j = vSampling.arrN(y) - 1
    //        while (j >= 0) {
    //          c0 = work.offset(x, vSampling.arrPixel(index))
    //          c = 0
    //          while (c < n_channel) {
    //            sample(c) += (work.readChannel(c0)) * vSampling.arrWeight(index)
    //            c0 += 1
    //            c += 1
    //          }
    //          index += 1
    //          j -= 1
    //        }
    //        c0 = out.offset(x, y)
    //        c = 0
    //        while (c < n_channel) {
    //          out.writeChannel(c0)(fit(sample(c), out.maxChannelValue))
    //          c += 1
    //          c0 += 1
    //        }
    //        y -= 1
    //      }
    //      x += delta
    //    }
  }

  private[this] def fit(f: Float, max: Int): Int = {
    if (f < 0) 0
    else if (f > max) max
    else (f + 0.5f).toInt
  }
}
