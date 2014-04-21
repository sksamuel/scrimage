package com.sksamuel.scrimage

import java.awt.image.BufferedImage
import thirdparty.mortennobel.ImageUtils

/** @author Stephen Samuel */
trait ScalingMethod {
  def samplingRadius: Float
}
trait Scaling {

  private var nrChannels: Int = 0
  private var srcWidth: Int = 0
  private var srcHeight: Int = 0
  private var dstWidth: Int = 0
  private var dstHeight: Int = 0

  private var horizontalSubsamplingData: SubSamplingData = null
  private var verticalSubsamplingData: SubSamplingData = null

  def doFilter(raster: Raster, width: Int, height: Int, method: ScalingMethod): Raster = {
    this.dstWidth = dstWidth
    this.dstHeight = dstHeight
    if (dstWidth < 3 || dstHeight < 3) {
      throw new RuntimeException(s"Error doing rescale. Target size was $width x $height but must be at least 3x3.")
    }

    this.nrChannels = 3
    assert(nrChannels > 0)

    this.srcWidth = raster.width
    this.srcHeight = raster.height

    var workPixels: Array[Array[Byte]] = new Array[Array[Byte]](srcHeight * width * nrChannels)

    horizontalSubsamplingData = createSubSampling(method, srcWidth, width)
    verticalSubsamplingData = createSubSampling(method, srcHeight, height)

    val scrImgCopy: BufferedImage = srcImg
    val workPixelsCopy: Array[Array[Byte]] = workPixels
    val threads: Array[Thread] = new Array[Thread](numberOfThreads - 1) {
      var i: Int = 1
      while (i < numberOfThreads) {
        {
          val finalI: Int = i
          threads(i - 1) = new Thread(new Runnable {
            def run {
              horizontallyFromSrcToWork(scrImgCopy, workPixelsCopy, finalI, numberOfThreads)
            }
          })
          threads(i - 1).start
        }
        ({
          i += 1;
          i - 1
        })
      }
    }
    horizontallyFromSrcToWork(scrImgCopy, workPixelsCopy, 0, numberOfThreads)
    waitForAllThreads(threads)
    val outPixels: Array[Byte] = new Array[Byte](dstWidth * dstHeight * nrChannels)
    val outPixelsCopy: Array[Byte] = outPixels {
      var i: Int = 1
      while (i < numberOfThreads) {
        {
          val finalI: Int = i
          threads(i - 1) = new Thread(new Runnable {
            def run {
              verticalFromWorkToDst(workPixelsCopy, outPixelsCopy, finalI, numberOfThreads)
            }
          })
          threads(i - 1).start
        }
        ({
          i += 1;
          i - 1
        })
      }
    }
    verticalFromWorkToDst(workPixelsCopy, outPixelsCopy, 0, numberOfThreads)
    waitForAllThreads(threads)
    workPixels = null
    var out: BufferedImage = null
    if (dest != null && dstWidth == dest.getWidth && dstHeight == dest.getHeight) {
      out = dest
      val nrDestChannels: Int = ImageUtils.nrChannels(dest)
      if (nrDestChannels != nrChannels) {
        val errorMgs: String = String
          .format("Destination image must be compatible width source image. Source image had %d channels " + "destination image had %d channels",
            nrChannels,
            nrDestChannels)
        throw new RuntimeException(errorMgs)
      }
    }
    else {
      out = new BufferedImage(dstWidth, dstHeight, getResultBufferedImageType(srcImg))
    }
    ImageUtils.setBGRPixels(outPixels, out, 0, 0, dstWidth, dstHeight)
    return out
  }

  private def createSubSampling(method: ScalingMethod,
                                srcSize: Int,
                                dstSize: Int): SubSamplingData = {
    val scale: Float = dstSize.asInstanceOf[Float] / srcSize.asInstanceOf[Float]
    val arrN: Array[Int] = new Array[Int](dstSize)
    var numContributors: Int = 0
    var arrWeight: Array[Float] = null
    var arrPixel: Array[Int] = null
    val fwidth: Float = method.samplingRadius
    val centerOffset: Float = 0.5f / scale
    if (scale < 1.0f) {
      val width: Float = fwidth / scale
      numContributors = (width * 2.0f + 2).asInstanceOf[Int]
      arrWeight = new Array[Float](dstSize * numContributors)
      arrPixel = new Array[Int](dstSize * numContributors)
      val fNormFac: Float = (1f / (Math.ceil(width) / fwidth)).asInstanceOf[Float] {
        var i: Int = 0
        while (i < dstSize) {
          {
            val subindex: Int = i * numContributors
            val center: Float = i / scale + centerOffset
            val left: Int = Math.floor(center - width).asInstanceOf[Int]
            val right: Int = Math.ceil(center + width).asInstanceOf[Int] {
              var j: Int = left
              while (j <= right) {
                {
                  var weight: Float = .0
                  weight = method.apply((center - j) * fNormFac)
                  if (weight == 0.0f) {
                    continue //todo: continue is not supported
                  }
                  var n: Int = 0
                  if (j < 0) {
                    n = -j
                  }
                  else if (j >= srcSize) {
                    n = srcSize - j + srcSize - 1
                  }
                  else {
                    n = j
                  }
                  val k: Int = arrN(i)
                  arrN(i) += 1
                  if (n < 0 || n >= srcSize) {
                    weight = 0.0f
                  }
                  arrPixel(subindex + k) = n
                  arrWeight(subindex + k) = weight
                }
                {
                  j += 1;
                  j - 1
                }
              }
            }
            val max: Int = arrN(i)
            var tot: Float = 0 {
              var k: Int = 0
              while (k < max) {
                tot += arrWeight(subindex + k) {
                  k += 1;
                  k - 1
                }
              }
            }
            if (tot != 0f) {
              {
                var k: Int = 0
                while (k < max) {
                  arrWeight(subindex + k) /= tot {
                    k += 1;
                    k - 1
                  }
                }
              }
            }
          }
          {
            i += 1;
            i - 1
          }
        }
      }
    }
    else {
      numContributors = (fwidth * 2.0f + 1).asInstanceOf[Int]
      arrWeight = new Array[Float](dstSize * numContributors)
      arrPixel = new Array[Int](dstSize * numContributors) {
        var i: Int = 0
        while (i < dstSize) {
          {
            val subindex: Int = i * numContributors
            val center: Float = i / scale + centerOffset
            val left: Int = Math.floor(center - fwidth).asInstanceOf[Int]
            val right: Int = Math.ceil(center + fwidth).asInstanceOf[Int] {
              var j: Int = left
              while (j <= right) {
                {
                  var weight: Float = method.apply(center - j)
                  if (weight == 0.0f) {
                    continue //todo: continue is not supported
                  }
                  var n: Int = 0
                  if (j < 0) {
                    n = -j
                  }
                  else if (j >= srcSize) {
                    n = srcSize - j + srcSize - 1
                  }
                  else {
                    n = j
                  }
                  val k: Int = arrN(i)
                  arrN(i) += 1
                  if (n < 0 || n >= srcSize) {
                    weight = 0.0f
                  }
                  arrPixel(subindex + k) = n
                  arrWeight(subindex + k) = weight
                }
                {
                  j += 1;
                  j - 1
                }
              }
            }
            val max: Int = arrN(i)
            var tot: Float = 0 {
              var k: Int = 0
              while (k < max) {
                tot += arrWeight(subindex + k) {
                  k += 1;
                  k - 1
                }
              }
            }
            assert(tot != 0, "should never happen except bug in filter")
            if (tot != 0f) {
              {
                var k: Int = 0
                while (k < max) {
                  arrWeight(subindex + k) /= tot {
                    k += 1;
                    k - 1
                  }
                }
              }
            }
          }
          {
            i += 1;
            i - 1
          }
        }
      }
    }
    SubSamplingData(arrN, arrPixel, arrWeight, numContributors)
  }
  private def verticalFromWorkToDst(workPixels: Array[Array[Byte]], outPixels: Array[Byte], start: Int, delta: Int) {
    if (nrChannels == 1) {
      verticalFromWorkToDstGray(workPixels, outPixels, start, numberOfThreads)
      return
    }
    val useChannel3: Boolean = nrChannels > 3
    {
      var x: Int = start
      while (x < dstWidth) {
        {
          val xLocation: Int = x * nrChannels
          {
            var y: Int = dstHeight - 1
            while (y >= 0) {
              {
                val yTimesNumContributors: Int = y * verticalSubsamplingData.numContributors
                val max: Int = verticalSubsamplingData.arrN(y)
                val sampleLocation: Int = (y * dstWidth + x) * nrChannels
                var sample0: Float = 0.0f
                var sample1: Float = 0.0f
                var sample2: Float = 0.0f
                var sample3: Float = 0.0f
                var index: Int = yTimesNumContributors
                {
                  var j: Int = max - 1
                  while (j >= 0) {
                    {
                      val valueLocation: Int = verticalSubsamplingData.arrPixel(index)
                      val arrWeight: Float = verticalSubsamplingData.arrWeight(index)
                      sample0 += (workPixels(valueLocation)(xLocation) & 0xff) * arrWeight
                      sample1 += (workPixels(valueLocation)(xLocation + 1) & 0xff) * arrWeight
                      sample2 += (workPixels(valueLocation)(xLocation + 2) & 0xff) * arrWeight
                      if (useChannel3) {
                        sample3 += (workPixels(valueLocation)(xLocation + 3) & 0xff) * arrWeight
                      }
                      index += 1
                    }
                    ({
                      j -= 1; j + 1
                    })
                  }
                }
                outPixels(sampleLocation) = toByte(sample0)
                outPixels(sampleLocation + 1) = toByte(sample1)
                outPixels(sampleLocation + 2) = toByte(sample2)
                if (useChannel3) {
                  outPixels(sampleLocation + 3) = toByte(sample3)
                }
              }
              ({
                y -= 1; y + 1
              })
            }
          }
        }
        x += delta
      }
    }
  }
  private def verticalFromWorkToDstGray(workPixels: Array[Array[Byte]],
                                        outPixels: Array[Byte],
                                        start: Int,
                                        delta: Int) {
    {
      var x: Int = start
      while (x < dstWidth) {
        {
          val xLocation: Int = x
          {
            var y: Int = dstHeight - 1
            while (y >= 0) {
              {
                val yTimesNumContributors: Int = y * verticalSubsamplingData.numContributors
                val max: Int = verticalSubsamplingData.arrN(y)
                val sampleLocation: Int = (y * dstWidth + x)
                var sample0: Float = 0.0f
                var index: Int = yTimesNumContributors
                {
                  var j: Int = max - 1
                  while (j >= 0) {
                    {
                      val valueLocation: Int = verticalSubsamplingData.arrPixel(index)
                      val arrWeight: Float = verticalSubsamplingData.arrWeight(index)
                      sample0 += (workPixels(valueLocation)(xLocation) & 0xff) * arrWeight
                      index += 1
                    }
                    ({
                      j -= 1; j + 1
                    })
                  }
                }
                outPixels(sampleLocation) = toByte(sample0)
              }
              ({
                y -= 1; y + 1
              })
            }
          }
        }
        x += delta
      }
    }
  }
  /**
   * Apply filter to sample horizontally from Src to Work
   */
  private def horizontallyFromSrcToWork(srcImg: BufferedImage, workPixels: Array[Array[Byte]], start: Int, delta: Int) {
    if (nrChannels == 1) {
      horizontallyFromSrcToWorkGray(srcImg, workPixels, start, delta)
      return
    }
    val tempPixels: Array[Int] = new Array[Int](srcWidth)
    val srcPixels: Array[Byte] = new Array[Byte](srcWidth * nrChannels)
    val useChannel3: Boolean = nrChannels > 3
    {
      var k: Int = start
      while (k < srcHeight) {
        {
          ImageUtils.getPixelsBGR(srcImg, k, srcWidth, srcPixels, tempPixels)
          {
            var i: Int = dstWidth - 1
            while (i >= 0) {
              {
                val sampleLocation: Int = i * nrChannels
                val max: Int = horizontalSubsamplingData.arrN(i)
                var sample0: Float = 0.0f
                var sample1: Float = 0.0f
                var sample2: Float = 0.0f
                var sample3: Float = 0.0f
                var index: Int = i * horizontalSubsamplingData.numContributors
                {
                  var j: Int = max - 1
                  while (j >= 0) {
                    {
                      val arrWeight: Float = horizontalSubsamplingData.arrWeight(index)
                      val pixelIndex: Int = horizontalSubsamplingData.arrPixel(index) * nrChannels
                      sample0 += (srcPixels(pixelIndex) & 0xff) * arrWeight
                      sample1 += (srcPixels(pixelIndex + 1) & 0xff) * arrWeight
                      sample2 += (srcPixels(pixelIndex + 2) & 0xff) * arrWeight
                      if (useChannel3) {
                        sample3 += (srcPixels(pixelIndex + 3) & 0xff) * arrWeight
                      }
                      index += 1
                    }
                    {
                      j -= 1;
                      j + 1
                    }
                  }
                }
                workPixels(k)(sampleLocation) = toByte(sample0)
                workPixels(k)(sampleLocation + 1) = toByte(sample1)
                workPixels(k)(sampleLocation + 2) = toByte(sample2)
                if (useChannel3) {
                  workPixels(k)(sampleLocation + 3) = toByte(sample3)
                }
              }
              ({
                i -= 1; i + 1
              })
            }
          }
        }
        k = k + delta
      }
    }
  }
  /**
   * Apply filter to sample horizontally from Src to Work
   */
  private def horizontallyFromSrcToWorkGray(srcImg: BufferedImage,
                                            workPixels: Array[Array[Byte]],
                                            start: Int,
                                            delta: Int) {
    val tempPixels: Array[Int] = new Array[Int](srcWidth)
    val srcPixels: Array[Byte] = new Array[Byte](srcWidth)
    {
      var k: Int = start
      while (k < srcHeight) {
        {
          ImageUtils.getPixelsBGR(srcImg, k, srcWidth, srcPixels, tempPixels)
          {
            var i: Int = dstWidth - 1
            while (i >= 0) {
              {
                val sampleLocation: Int = i
                val max: Int = horizontalSubsamplingData.arrN(i)
                var sample0: Float = 0.0f
                var index: Int = i * horizontalSubsamplingData.numContributors
                {
                  var j: Int = max - 1
                  while (j >= 0) {
                    {
                      val arrWeight: Float = horizontalSubsamplingData.arrWeight(index)
                      val pixelIndex: Int = horizontalSubsamplingData.arrPixel(index)
                      sample0 += (srcPixels(pixelIndex) & 0xff) * arrWeight
                      index += 1
                    }
                    ({
                      j -= 1; j + 1
                    })
                  }
                }
                workPixels(k)(sampleLocation) = toByte(sample0)
              }
              ({
                i -= 1; i + 1
              })
            }
          }
        }
        k = k + delta
      }
    }
  }
}

case class SubSamplingData(arrN: Array[Int], arrPixel: Array[Int], arrWeight: Array[Float], numContributors: Int)