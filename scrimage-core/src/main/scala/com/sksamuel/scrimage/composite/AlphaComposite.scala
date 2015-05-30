package com.sksamuel.scrimage.composite

import com.sksamuel.scrimage.{ Image, Composite }
import java.awt.Graphics2D

/** @author Stephen Samuel */
class AlphaComposite(alpha: Double) extends Composite {
  def apply(src: Image, applicative: Image): Unit = {
    val g2 = src.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setComposite(java.awt.AlphaComposite.SrcOver.derive(alpha.toFloat))
    g2.drawImage(applicative.awt, 0, 0, null)
    g2.dispose()
  }
}
object AlphaComposite {
  def apply(alpha: Double) = new AlphaComposite(alpha)
}
