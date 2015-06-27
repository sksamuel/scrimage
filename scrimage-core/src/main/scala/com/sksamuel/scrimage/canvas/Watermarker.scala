package com.sksamuel.scrimage.canvas

import java.awt.geom.AffineTransform
import java.awt.{AlphaComposite, Graphics2D, RenderingHints}

import com.sksamuel.scrimage.{Color, Image, X11Colorlist}

import scala.language.implicitConversions

class Watermarker(image: Image) {

  def repeated(text: String, style: TextStyle): Image = {
    require(style.size >= 6, "Font size must be >= 6")
    val target = image.copy

    val g2 = target.awt.getGraphics.asInstanceOf[Graphics2D]
    setupGraphics(g2, style)

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

    target
  }

  private def setupGraphics(g2: Graphics2D, style: TextStyle): Unit = {
    if (style.antiAlias)
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
    g2.setColor(style.color)
    val alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, style.alpha.toFloat)
    g2.setComposite(alphaComposite)
    g2.setFont(new java.awt.Font(style.font.name, 0, style.size))
  }

  def at(text: String, x: Int, y: Int, style: TextStyle): Image = {
    require(style.size >= 6, "Font size must be >= 6")

    val target = image.copy
    val g2 = target.awt.getGraphics.asInstanceOf[Graphics2D]
    setupGraphics(g2, style)

    g2.drawString(text, x, y)
    g2.dispose()

    target
  }

  def centered(text: String, style: TextStyle): Image = {
    require(style.size >= 6, "Font size must be >= 6")

    val target = image.copy
    val g2 = target.awt.getGraphics.asInstanceOf[Graphics2D]
    setupGraphics(g2, style)

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

    target
  }
}

object Watermarker {
  implicit class RichImage(image: Image) {
    def watermarker: Watermarker = new Watermarker(image)
  }
}



