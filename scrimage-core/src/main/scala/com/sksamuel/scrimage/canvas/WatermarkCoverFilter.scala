package com.sksamuel.scrimage.canvas

import java.awt.{AlphaComposite, Graphics2D, RenderingHints}

import com.sksamuel.scrimage.{Color, Filter, Image}

import scala.language.implicitConversions

/**
 * Places a watermark on the image repeated so that it covers the image.
 */
class WatermarkCoverFilter(text: String,
                           size: Int = 12,
                           font: Font = Font.SansSerif,
                           alpha: Double = 0.1,
                           color: Color = Color.White) extends Filter {
  require(size > 0, "Font size must be > 0")

  def apply(image: Image): Unit = {

    val g2 = image.awt.getGraphics.asInstanceOf[Graphics2D]
    setupGraphics(g2)

    val fontMetrics = g2.getFontMetrics
    val bounds = fontMetrics.getStringBounds(text + " ", g2)

    var x = 0
    var xstart = 0
    var y = bounds.getY.toInt
    while (y < image.height + bounds.getHeight) {
      while (x < image.width + bounds.getWidth) {
        g2.drawString(text, x, y)
        x = x + bounds.getWidth.toInt
      }
      y = y + (bounds.getHeight + bounds.getHeight * 0.2).toInt
      xstart = (xstart - bounds.getWidth * 0.2f).toInt
      x = xstart
    }

    g2.dispose()
  }

  private def setupGraphics(g2: Graphics2D): Unit = {
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
    g2.setColor(color)
    val alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha.toFloat)
    g2.setComposite(alphaComposite)
    g2.setFont(new java.awt.Font(font.name, 0, size))
  }
}