package com.sksamuel.scrimage


import scala.collection.JavaConversions._


object ResampleOpScala {

    val MAX_CHANNEL_VALUE = 255

    case class ResampFilter(samplingRadius: Int, f: Float => Float){
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

    val bicubicFilter = ResampFilter(2, bicubicInterpolation(0.5f))

    case class SubSamplingData(
        arrN: Array[Int],
        arrPixel: Array[Int],
        arrWeight: Array[Float],
        numContributors: Int)

    def createSubSampling(filter: ResampFilter, srcSize: Int, dstSize: Int): SubSamplingData = {
        val scale = dstSize.toFloat / srcSize.toFloat
        val arrN = Array.ofDim[Int](dstSize)
        var numContributors: Int = 0
        var arrWeight: Array[Float] = null
        var arrPixel: Array[Int] = null
        val fwidth = filter.samplingRadius
        val centerOffset = 0.5f / scale
        if (scale < 1.0f) {
            val width = fwidth / scale
            numContributors = (width * 2.0f + 2).toInt
            arrWeight = Array.ofDim[Float](dstSize * numContributors)
            arrPixel = Array.ofDim[Int](dstSize * numContributors)
            val fNormFac = (1f / (Math.ceil(width) / fwidth)).toFloat
            for (i <- 0 until dstSize) {
                val subindex = i * numContributors
                val center = i / scale + centerOffset
                val left = Math.floor(center - width).toInt
                val right = Math.ceil(center + width).toInt
                for (j <- left to right) {
                    var weight = filter((center - j) * fNormFac)
                    if (weight != 0.0f) {
                        val n = (if (j < 0) -j
                              else if (j >= srcSize) srcSize - j + srcSize - 1
                              else j)
                        val k = arrN(i)
                        arrN(i) += 1
                        if (n < 0 || n >= srcSize) weight = 0.0f
                        arrPixel(subindex + k) = n
                        arrWeight(subindex + k) = weight
                    }
                }
                val max = arrN(i)
                var tot = 0f
                for (k <- 0 until max) tot += arrWeight(subindex + k)
                if (tot != 0f) {
                    for (k <- 0 until max) arrWeight(subindex + k) /= tot
                }
            }
        } else {
          numContributors = (fwidth * 2.0f + 1).toInt
          arrWeight = Array.ofDim[Float](dstSize * numContributors)
          arrPixel = Array.ofDim[Int](dstSize * numContributors)
          for (i <- 0 until dstSize) {
              val subindex = i * numContributors
              val center = i / scale + centerOffset
              val left = Math.floor(center - fwidth).toInt
              val right = Math.ceil(center + fwidth).toInt
              for (j <- left to right) {
                  var weight = filter(center - j)
                  if (weight != 0.0f) {
                      val n: Int = (
                          if (j < 0) -j
                          else if (j >= srcSize) srcSize - j + srcSize - 1
                          else j)
                      val k = arrN(i)
                      arrN(i) += 1
                      if (n < 0 || n >= srcSize) weight = 0.0f
                      arrPixel(subindex + k) = n
                      arrWeight(subindex + k) = weight
                  }
              }
              val max = arrN(i)
              var tot = 0f
              for (k <- 0 until max) tot += arrWeight(subindex + k)
              assert(tot != 0) // "should never happen except bug in filter"
              if (tot != 0f) {
                  for (k <- 0 until max) arrWeight(subindex + k) /= tot
              }
          }
        }
        SubSamplingData(arrN, arrPixel, arrWeight, numContributors)
    }

    def waitForAllThreads(threads: Array[Thread]) {
        try {
            for (t <- threads){
                t.join(java.lang.Long.MAX_VALUE)

            }
        } catch {
            case e: InterruptedException => {
                e.printStackTrace()
                throw new RuntimeException(e)
            }
        }
    }

    def scaleTo(filter: ResampFilter)(img: Image)(dstWidth:Int, dstHeight:Int, numberOfThreads:Int = 0): Image = {
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

        val horizontalSubsamplingData = createSubSampling(filter, srcWidth, dstWidth)
        val verticalSubsamplingData = createSubSampling(filter, srcHeight, dstHeight)
        val workPixels = Array.ofDim[Int](srcHeight, dstWidth * nrChannels)

        val srcRaster = img.raster
        type PixelType = srcRaster.PixelType

        def horizontallyFromSrcToWork(
                workPixels: Array[Array[Int]],
                start: Int,
                delta: Int) {
            val srcPixels: Array[PixelType] = srcRaster.newDataModel(srcWidth)
            var k = start
            while (k < srcHeight) {
              getPixels[PixelType](srcRaster.model, k, srcWidth, srcPixels)
              var i = dstWidth - 1
              while (i >= 0) {
                val sampleLocation = i * nrChannels
                val max = horizontalSubsamplingData.arrN(i)
                val samples = Array.ofDim[Float](nrChannels)
                var index = i * horizontalSubsamplingData.numContributors
                var j = max - 1
                while (j >= 0) {
                  val arrWeight = horizontalSubsamplingData.arrWeight(index)
                  val pixelIndex = horizontalSubsamplingData.arrPixel(index)
                  val pixel = srcRaster.unpack(srcPixels(pixelIndex))
                  for(comp <- 0 until nrChannels){
                    samples(comp) += pixel(comp) * arrWeight
                  }
                  index += 1
                  j -= 1
                }
                for(comp <- 0 until nrChannels){
                    workPixels(k)(sampleLocation + comp) = toByte(samples(comp))
                }
                i -= 1
              }
              k = k + delta
            }
        }

        def verticalFromWorkToDst(
            workPixels: Array[Array[Int]],
            outPixels: Array[PixelType],
            start: Int,
            delta: Int) {

            var x = start
            while (x < dstWidth) {
              val xLocation = x * nrChannels
              var y = dstHeight - 1
              while (y >= 0) {
                val yTimesNumContributors = y * verticalSubsamplingData.numContributors
                val max = verticalSubsamplingData.arrN(y)
                val sampleLocation = y * dstWidth + x
                val samples = Array.ofDim[Float](nrChannels)
                var index = yTimesNumContributors
                var j = max - 1
                while (j >= 0) {
                  val valueLocation = verticalSubsamplingData.arrPixel(index)
                  val arrWeight = verticalSubsamplingData.arrWeight(index)

                  for(comp <- 0 until nrChannels){
                    samples(comp) += (workPixels(valueLocation)(xLocation + comp) & 0xff) * arrWeight
                  }
                  index += 1
                  j -= 1
                }
                outPixels(sampleLocation) = srcRaster.pack(samples.map(toUByte))
                y -= 1
              }
              x += delta
            }
        }

        def toByte(f: Float): Byte = {
            if (f < 0) {
              return 0
            }
            if (f > MAX_CHANNEL_VALUE) {
              return MAX_CHANNEL_VALUE.toByte
            }
            (f + 0.5f).toByte
        }
        def toUByte(f: Float): Int = {
            if (f < 0) {
              return 0
            }
            if (f > MAX_CHANNEL_VALUE) {
              return MAX_CHANNEL_VALUE
            }
            (f + 0.5f).toInt
        }


        val threads = Array.ofDim[Thread](numberOfThreads - 1)
        for (i <- 1 until numberOfThreads) {
          val finalI = i
          threads(i - 1) = new Thread(new Runnable() {
              def run() {
                  horizontallyFromSrcToWork(workPixels, finalI, numberOfThreads)
              }
          })
          threads(i - 1).start()
        }
        horizontallyFromSrcToWork(workPixels, 0, numberOfThreads)
        waitForAllThreads(threads)
        val outPixels = srcRaster.newDataModel(dstWidth * dstHeight)
        for (i <- 1 until numberOfThreads) {
          val finalI = i
          threads(i - 1) = new Thread(new Runnable() {
            def run() {
              verticalFromWorkToDst(workPixels, outPixels, finalI, numberOfThreads)
            }
          })
          threads(i - 1).start()
        }
        verticalFromWorkToDst(workPixels, outPixels, 0, numberOfThreads)
        waitForAllThreads(threads)
        new Image(srcRaster.copyWith(dstWidth, dstHeight, outPixels))
    }

    /**
     * returns one row of image data
     */
    def getPixels[T](data: Array[T], y:  Int, w: Int, out: Array[T]): Array[T] = {
      System.arraycopy(data, y*w, out, 0, w)
      return out;
    }
}
