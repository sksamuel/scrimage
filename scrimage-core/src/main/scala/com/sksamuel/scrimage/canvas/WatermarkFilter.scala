package com.sksamuel.scrimage.canvas

import java.awt.{AlphaComposite, RenderingHints, Graphics2D}

import com.sksamuel.scrimage.{Image, Color, Filter}

/**
 * Places a watermark at a given location.
 */
class WatermarkFilter(text: String,
                      x: Int,
                      y: Int,
                      color: Color = Color.White,
                      size: Int = 18,
                      font: Font = Font.SansSerif,
                      alpha: Double = 0.1)
  extends Filter {
  require(size > 0, "Font size must be > 0")

  private def setupGraphics(g2: Graphics2D): Unit = {
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
    g2.setColor(color)
    val alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha.toFloat)
    g2.setComposite(alphaComposite)
    g2.setFont(new java.awt.Font(font.name, 0, size))
  }

  override def apply(image: Image): Unit = {
    val g2 = image.awt.getGraphics.asInstanceOf[Graphics2D]
    setupGraphics(g2)

    g2.drawString(text, x, y)
    g2.dispose()
  }
}
