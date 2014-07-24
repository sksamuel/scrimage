package com.sksamuel.scrimage


trait Interpolator {
    val raster: Raster

    def extract(x: Float, y: Float): Array[Int]
    def interpolateFrom(x: Float, y: Float)(extracted: Array[Int]): Int

    def interpolate(x: Float, y: Float): Int = {
        val extracted : Array[Array[Int]] = extract(x, y).map(raster.listComponents)
        def slice(i: Int): Array[Int] = extracted.map(_.apply(i))
        raster.fromComponents(
            Array.range(0, extracted.length)
            .map(slice)
            .map(interpolateFrom(x, y)))
    }

    def scaleTo(targetWidth: Int, targetHeight: Int): Image = {
        val scaled = raster.rasterOfSize(targetWidth, targetHeight)
        val ratioX = 1f * raster.width / targetWidth
        val ratioY = 1f * raster.height / targetHeight
        for(x <- 0 until targetWidth; y <- 0 until targetHeight){
            scaled.write(x, y, interpolate(ratioX * x, ratioY * y))
        }
        new Image(scaled)
    }

    protected def coerce(c: Float) : Int = {
        if(c > 255) 255
        else if(c < 0) 0
        else c.toInt
    }
}

class NearestNeighbor(val image: Image) extends Interpolator {
    val raster = image.raster

    def extract(x: Float, y: Float) = Array(raster.pixel(x.toInt, y.toInt))

    def interpolateFrom(x: Float, y: Float)(extracted: Array[Int]) = extracted(0)

    override def interpolate(x: Float, y: Float): Int = raster.pixel(x.toInt, y.toInt)
}

class Bilinear(val image: Image) extends Interpolator {
    val raster = image.raster

    def extract(x: Float, y: Float) = {
        val x1 = math.min(x.toInt, raster.width - 2)
        val y1 = math.min(y.toInt, raster.height - 2)
        Array(raster.pixel(x1, y1),
              raster.pixel(x1+1, y1),
              raster.pixel(x1, y1+1),
              raster.pixel(x1+1, y1+1))
    }

    def interpolateFrom(x: Float, y: Float)(extracted: Array[Int]) = {
        val x1 = x - math.min(x.toInt, raster.width - 2)
        val y1 = y - math.min(y.toInt, raster.height - 2)
        coerce(
          (1 - x1) * (1 - y1) * extracted(0) +
          x1 * (1 - y1) * extracted(1) +
          (1 - x1) * y1 * extracted(2) +
          x1 * y1 * extracted(3)
        )
    }
}
