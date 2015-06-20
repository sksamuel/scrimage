package com.sksamuel.scrimage.composite

import java.awt.Graphics2D

import com.sksamuel.scrimage.{AbstractImage, Composite}

/** @author Stephen Samuel */
class AlphaComposite(alpha: Double) extends Composite {
  def apply(src: AbstractImage, applicative: AbstractImage): Unit = {
    val g2 = src.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setComposite(java.awt.AlphaComposite.SrcOver.derive(alpha.toFloat))
    g2.drawImage(applicative.awt, 0, 0, null)
    g2.dispose()
  }
}
object AlphaComposite {
  def apply(alpha: Double): AlphaComposite = new AlphaComposite(alpha)
}
