package com.sksamuel.scrimage.scaling

import java.awt.{RenderingHints, Graphics2D}
import java.awt.image.{BufferedImage, DataBufferInt}

trait Scale {
  def scale(in: BufferedImage, w: Int, h: Int): BufferedImage
}

object ScrimageNearestNeighbourScale extends Scale {

  override def scale(in: BufferedImage, w: Int, h: Int): BufferedImage = {

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

object AwtNearestNeighbourScale extends Scale {
  override def scale(in: BufferedImage, w: Int, h: Int): BufferedImage = {
    val target = new BufferedImage(w, h, in.getType)
    val g2 = target.getGraphics.asInstanceOf[Graphics2D]
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
    g2.drawImage(in, 0, 0, w, h, null)
    g2.dispose()
    target
  }
}