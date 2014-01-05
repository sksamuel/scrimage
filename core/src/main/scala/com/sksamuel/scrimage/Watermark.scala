package com.sksamuel.scrimage

import java.awt._
import java.awt.geom.AffineTransform

/** @author Stephen Samuel */
class Watermark(text: String, fontSize: Int, alpha: Double) extends Filter {

  def apply(image: Image): Unit = {

    val g2 = image.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setColor(Color.white)
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

    val alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha.toFloat)
    g2.setComposite(alphaComposite)

    val f1 = new Font("Arial", Font.BOLD, fontSize)
    g2.setFont(f1)

    val fontMetrics = g2.getFontMetrics
    val bounds = fontMetrics.getStringBounds(text, g2)

    g2.translate(image.width / 2.0, image.height / 2.0)

    val rotation = new AffineTransform()
    val opad = image.height / image.width.toDouble
    val angle = Math.toDegrees(Math.atan(opad))
    val idegrees = -1 * angle
    val theta = (2 * Math.PI * idegrees) / 360
    rotation.rotate(theta)
    g2.transform(rotation)

    val x1 = bounds.getWidth / 2.0f * -1
    val y1 = bounds.getHeight / 2.0f
    g2.translate(x1, y1)

    g2.drawString(text, 0.0f, 0.0f)
    g2.dispose()
  }
}
