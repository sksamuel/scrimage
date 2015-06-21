package com.sksamuel.scrimage.canvas

import java.awt.{Font, Graphics2D, RenderingHints}

import com.sksamuel.scrimage.{Color, Image, X11Colorlist}

import scala.language.implicitConversions

/** @author Stephen Samuel */
case class Canvas(image: Image,
                  painter: Painter = X11Colorlist.Black,
                  font: java.awt.Font = new java.awt.Font(java.awt.Font.SERIF, 0, 24),
                  aliasing: Boolean = false) {

  def withPainter(painter: Painter): Canvas = copy(painter = painter)
  def withPainter(color: Color): Canvas = copy(painter = color)
  def withFont(font: java.awt.Font): Canvas = copy(font = font)
  def withAliasing(aliasing: Boolean): Canvas = copy(aliasing = aliasing)

  private def g2(image: Image): Graphics2D = {
    val g2 = image.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setPaint(painter.paint)
    if (aliasing) {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
    }
    g2.setFont(font)
    g2
  }

  def draw(drawables: Drawable*): Canvas = {
    val target = image.copy
    val g = g2(target)
    drawables.foreach(_.draw(g))
    g.dispose()
    target
  }

  def draw(drawables: Iterable[Drawable]): Canvas = {
    val target = image.copy
    val g = g2(target)
    drawables.foreach(_.draw(g))
    g.dispose()
    target
  }

  @deprecated("use image.watermarker.method", "2.1.0")
  def watermark(text: String): Canvas = watermark(text, 0.5)

  @deprecated("use image.watermarker.method", "2.1.0")
  def watermark(text: String, alpha: Double): Canvas = image.filter(new WatermarkFilter(text, font, alpha))
}

object Canvas {
  implicit def image2canvas(image: Image): Canvas = new Canvas(image)
  implicit def canvas2image(canvas: Canvas): Image = canvas.image
}

