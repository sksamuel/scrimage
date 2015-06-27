package com.sksamuel.scrimage.canvas

import java.awt.{AlphaComposite, Graphics2D, RenderingHints}

import com.sksamuel.scrimage.Position.BottomLeft
import com.sksamuel.scrimage.{Position, Color, Filter, Image}

import scala.language.implicitConversions

class CaptionFilter(text: String,
                    size: Int = 18,
                    position: Position = BottomLeft,
                    x: Int = -1,
                    y: Int = -1,
                    font: Font = Font.as(java.awt.Font.SANS_SERIF),
                    textColor: Color = Color.White,
                    textAlpha: Double = 0.1d,
                    antiAlias: Boolean = true,
                    fullWidth: Boolean = false,
                    captionBackground: Color = Color.White,
                    captionAlpha: Double = 0.1,
                    padding: Padding = Padding(10)) extends Filter {
  require(size > 0, "Font size must be > 0")

  def apply(image: Image): Unit = {

    val g2 = image.awt.getGraphics.asInstanceOf[Graphics2D]

    if (antiAlias) {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
      g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY)
      g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
    }
    g2.setFont(new java.awt.Font(font.name, 0, size))

    val bounds = g2.getFontMetrics.getStringBounds(text, g2)
    val descent = g2.getFontMetrics.getDescent
    val captionWidth = if (fullWidth) image.width else bounds.getWidth.toInt + padding.left + padding.right
    val captionHeight = bounds.getHeight.toInt + padding.top + padding.bottom

    // captionx/y are the top/left coordinates for the caption box
    val (captionX, captionY) = if (x == -1 || y == -1) {
      position.calculateXY(image.width, image.height, captionWidth, captionHeight)
    } else {
      (x, y)
    }

    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, captionAlpha.toFloat))
    g2.setColor(captionBackground)
    g2.fillRect(captionX, image.height - captionHeight, captionWidth, captionHeight)

    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, textAlpha.toFloat))
    g2.setColor(textColor)
    g2.drawString(text, captionX + padding.left, captionY + padding.top + g2.getFontMetrics.getHeight - descent)
    g2.dispose()
  }
}
