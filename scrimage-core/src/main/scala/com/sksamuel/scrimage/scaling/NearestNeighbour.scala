package com.sksamuel.scrimage.scaling

import java.awt.image.{BufferedImage, DataBufferInt}

object NearestNeighbour {

  def scale(in: BufferedImage, w: Int, h: Int): BufferedImage = {

    val out = new BufferedImage(w, h, in.getType)
    val pixels = in.getRaster.getDataBuffer.asInstanceOf[DataBufferInt].getData
    val newpixels = out.getRaster.getDataBuffer.asInstanceOf[DataBufferInt].getData

    var n = 0
    var k = 0d
    val ow = in.getWidth
    val xr = in.getWidth / w.toDouble
    val yr = in.getHeight / h.toDouble
    for ( y <- 0 until h ) {
      for ( x <- 0 until w ) {
        newpixels(n) = pixels(k.toInt)
        k = k + xr
        n = n + 1
      }
      k = ow * (y * yr).toInt
    }
    out
  }
}
