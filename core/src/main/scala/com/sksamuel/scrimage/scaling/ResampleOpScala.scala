package com.sksamuel.scrimage.scaling

import com.sksamuel.scrimage.Image

object ResampleOpScala {

  private val MAX_CHANNEL_VALUE = 255

  case class ResampFilter(samplingRadius: Int, f: Float => Float) {
    def apply(x: Float) = f(x)
  }

  def bicubicInterpolation(a: Float)(x: Float): Float = {
    if (x == 0) 1.0f
    else if (x < 0.0f) bicubicInterpolation(a)(-x)
    else {
      val xx = x * x
      if (x < 1.0f) (a + 2f) * xx * x - (a + 3f) * xx + 1f
      else if (x < 2.0f) a * xx * x - 5 * a * xx + 8 * a * x - 4 * a
      else 0.0f
    }
  }

  val bicubicFilter = ResampFilter(2, bicubicInterpolation(-0.5f))

  case class SubSamplingData(
                              arrN: Array[Int],
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

  private[this] def waitForAllThreads(threads: Array[Thread]) {
    try {
      for ( t <- threads ) {
        t.join(java.lang.Long.MAX_VALUE)

      }
    } catch {
      case e: InterruptedException =>
        e.printStackTrace()
        throw new RuntimeException(e)
    }
  }

  def scaleTo(filter: ResampFilter)(img: Image)(dstWidth: Int, dstHeight: Int, numberOfThreads: Int = 0): Image = {
    if (dstWidth < 3 || dstHeight < 3) {
      throw new RuntimeException("Error doing rescale. Target size was " + dstWidth + "x" +
        dstHeight +
        " but must be" +
        " at least 3x3.")
    }
    val nrChannels = img.raster.n_comp
    assert(nrChannels > 0)
    val srcWidth = img.width
    val srcHeight = img.height

    val hSampling = createSubSampling(filter, srcWidth, dstWidth)
    val vSampling = createSubSampling(filter, srcHeight, dstHeight)

    val srcRaster = img.raster

    val srcPixels: Array[Array[Byte]] = srcRaster.unpack()

    val outRaster = srcRaster.empty(dstWidth, dstHeight)
    val middleRaster = srcRaster.empty(dstWidth, srcHeight)

    for ( comp <- 0 until srcRaster.n_comp ) {
      val threads = Array.ofDim[Thread](numberOfThreads - 1)
      val workPixels = Array.ofDim[Byte](srcHeight * dstWidth)
      for ( i <- 1 until numberOfThreads ) {
        val finalI = i
        threads(i - 1) = new Thread(new Runnable() {
          def run() {
            horizontallyFromSrcToWork(
              srcPixels(comp), workPixels,
              srcWidth, srcHeight, dstWidth,
              finalI, numberOfThreads, hSampling)
          }
        })
        threads(i - 1).start()
      }
      horizontallyFromSrcToWork(
        srcPixels(comp), workPixels,
        srcWidth, srcHeight, dstWidth,
        0, numberOfThreads, hSampling)
      waitForAllThreads(threads)
      val outPixels = Array.ofDim[Byte](dstWidth * dstHeight)
      for ( i <- 1 until numberOfThreads ) {
        val finalI = i
        threads(i - 1) = new Thread(new Runnable() {
          def run() {
            verticalFromWorkToDst(
              workPixels, outPixels,
              dstWidth, dstHeight,
              finalI, numberOfThreads,
              vSampling)
          }
        })
        threads(i - 1).start()
      }
      verticalFromWorkToDst(
        workPixels, outPixels,
        dstWidth, dstHeight,
        0, numberOfThreads,
        vSampling)
      waitForAllThreads(threads)
      outRaster.foldComp(comp)(outPixels)
    }
    new Image(outRaster)
  }

  private[this] def horizontallyFromSrcToWork(
                                               srcPixels: Array[Byte], workPixels: Array[Byte],
                                               srcWidth: Int, srcHeight: Int, dstWidth: Int,
                                               start: Int, delta: Int,
                                               hSampling: SubSamplingData) {
    var y = start
    var x = 0
    var j = 0
    var index = 0
    var max = 0
    var sample = 0f
    while (y < srcHeight) {
      x = dstWidth - 1
      j = 0
      while (x >= 0) {
        max = hSampling.arrN(x)
        index = x * hSampling.numContributors
        sample = 0
        j = max - 1
        while (j >= 0) {
          sample += (srcPixels(y * srcWidth + hSampling.arrPixel(index)) & 0xff) * hSampling.arrWeight(index)
          index += 1
          j -= 1
        }
        workPixels(y * dstWidth + x) = toByte(sample)
        x -= 1
      }
      y = y + delta
    }
  }

  private[this] def verticalFromWorkToDst(
                                           workPixels: Array[Byte], outPixels: Array[Byte],
                                           dstWidth: Int, dstHeight: Int,
                                           start: Int, delta: Int,
                                           vSampling: SubSamplingData) {

    var x = start
    var y = 0
    var max = 0
    var sample = 0f
    var index = 0
    var j = 0
    while (x < dstWidth) {
      y = dstHeight - 1
      while (y >= 0) {
        max = vSampling.arrN(y)
        sample = 0
        index = y * vSampling.numContributors
        j = max - 1
        while (j >= 0) {
          sample += (workPixels(vSampling.arrPixel(index) * dstWidth + x) & 0xff) * vSampling.arrWeight(index)
          index += 1
          j -= 1
        }
        outPixels(y * dstWidth + x) = toByte(sample)
        y -= 1
      }
      x += delta
    }
  }

  private[this] def toByte(f: Float): Byte = {
    if (f < 0) 0.toByte
    else if (f > MAX_CHANNEL_VALUE) MAX_CHANNEL_VALUE.toByte
    else (f + 0.5f).toByte
  }
}
