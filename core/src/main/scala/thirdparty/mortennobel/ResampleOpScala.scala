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
                        n = (if (j < 0) -j
                            else if (j >= srcSize) srcSize - j + srcSize - 1
                            else j)
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
                k=0
                while (k < max){tot += arrWeight(subindex + k); k+=1}
                if (tot != 0f) {
                    k=0
                    while (k < max){arrWeight(subindex + k) /= tot; k+=1}
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
                while(j <= right) {
                    weight = filter(center - j)
                    if (weight != 0.0f) {
                        val n: Int = (
                            if (j < 0) -j
                            else if (j >= srcSize) srcSize - j + srcSize - 1
                            else j)
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
                k=0
                while (k < max){ tot += arrWeight(subindex + k); k+=1}
                assert(tot != 0) // "should never happen except bug in filter"
                if (tot != 0f) {
                    k=0
                    while (k < max){arrWeight(subindex + k) /= tot; k+=1}
                }
                i += 1
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

        val hSubsampling = createSubSampling(filter, srcWidth, dstWidth)
        val vSubsampling = createSubSampling(filter, srcHeight, dstHeight)

        val srcRaster = img.raster
        type PixelType = srcRaster.PixelType
        val workPixels = srcRaster.newDataModel(srcHeight * dstWidth)

        def horizontallyFromSrcToWork(
                workPixels: Array[PixelType],
                start: Int,
                delta: Int) {
            val srcPixels: Array[PixelType] = srcRaster.newDataModel(srcWidth)
            var k = start
            var i = 0
            var j = 0
            var index = 0
            var max = 0
            var comp = 0
            val pixel = Array.ofDim[Int](nrChannels)
            while (k < srcHeight) {
                getPixels[PixelType](srcRaster.model, k, srcWidth, srcPixels)
                i = dstWidth - 1
                j = 0
                while (i >= 0) {
                    max = hSubsampling.arrN(i)
                    index = i * hSubsampling.numContributors
                    val samples = Array.ofDim[Float](nrChannels)
                    j = max - 1
                    while (j >= 0) {
                        srcRaster.unpack(srcPixels(hSubsampling.arrPixel(index)), pixel)
                        comp=0
                        while(comp < nrChannels){
                            samples(comp) += pixel(comp) * hSubsampling.arrWeight(index)
                            comp += 1
                        }
                        index += 1
                        j -= 1
                    }
                    workPixels(k * dstWidth + i) = srcRaster.pack(samples.map(toUByte))
                    i -= 1
                }
                k = k + delta
            }
        }

        def verticalFromWorkToDst(
                workPixels: Array[PixelType],
                outPixels: Array[PixelType],
                start: Int,
                delta: Int) {

            var x = start
            var comp = 0
            var y = 0
            var max = 0
            var index = 0
            var j = 0
            val px = Array.ofDim[Int](nrChannels)
            while (x < dstWidth) {
                y = dstHeight - 1
                while (y >= 0) {
                    max = vSubsampling.arrN(y)
                    val samples = Array.ofDim[Float](nrChannels)
                    index = y * vSubsampling.numContributors
                    j = max - 1
                    while (j >= 0) {
                        srcRaster.unpack(workPixels(vSubsampling.arrPixel(index)*dstWidth + x), px)
                        comp = 0
                        while(comp < nrChannels){
                            samples(comp) += px(comp) * vSubsampling.arrWeight(index)
                            comp += 1
                        }
                        index += 1
                        j -= 1
                    }
                    outPixels(y * dstWidth + x) = srcRaster.pack(samples.map(toUByte))
                    y -= 1
                }
                x += delta
            }
        }

        def toByte(f: Float): Byte = {
            if (f < 0) 0
            else if (f > MAX_CHANNEL_VALUE) MAX_CHANNEL_VALUE.toByte
            else (f + 0.5f).toByte
        }
        def toUByte(f: Float): Int = {
            if (f < 0) 0
            else if (f > MAX_CHANNEL_VALUE) MAX_CHANNEL_VALUE
            else (f + 0.5f).toInt
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
