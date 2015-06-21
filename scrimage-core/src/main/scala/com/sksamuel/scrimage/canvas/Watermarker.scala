package com.sksamuel.scrimage.canvas

import java.awt.{AlphaComposite, Graphics2D, RenderingHints}

import com.sksamuel.scrimage.{Color, Image, X11Colorlist}

import scala.language.implicitConversions

class Watermarker(image: Image) {

  def repeated(text: String, style: TextStyle): Image = {
    require(style.size >= 6, "Font size must be >= 6")
    val target = image.copy

    val g2 = target.awt.getGraphics.asInstanceOf[Graphics2D]

    if (style.antiAlias)
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

    val alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, style.alpha.toFloat)
    g2.setComposite(alphaComposite)
    g2.setColor(style.color)

    g2.setFont(new java.awt.Font(style.font.name, style.font.style, style.size))

    val fontMetrics = g2.getFontMetrics
    val bounds = fontMetrics.getStringBounds(text + " ", g2)
    println(bounds)

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

    target
  }
}

object Watermarker {
  implicit class RichImage(image: Image) {
    def watermarker: Watermarker = new Watermarker(image)
  }
}

case class Font(name: String = java.awt.Font.SANS_SERIF, bold: Boolean = true, italic: Boolean = false) {
  def style: Int = {
    var k = java.awt.Font.PLAIN
    if (bold) k = k | java.awt.Font.BOLD
    if (italic) k = k | java.awt.Font.ITALIC
    k
  }
}

case class TextStyle(size: Int = 18,
                     font: Font = Font(java.awt.Font.SANS_SERIF),
                     alpha: Double = 0.1d,
                     antiAlias: Boolean = true,
                     color: Color = X11Colorlist.White)