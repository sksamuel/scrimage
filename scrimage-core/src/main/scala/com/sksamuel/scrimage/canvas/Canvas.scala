package com.sksamuel.scrimage.canvas

import java.awt.Graphics2D

import com.sksamuel.scrimage.Image

import scala.language.implicitConversions

/** @author Stephen Samuel */
case class Canvas(image: Image) {

  private def g2(image: Image): Graphics2D = image.awt.getGraphics.asInstanceOf[Graphics2D]

  protected[scrimage] def drawInPlace(drawables: Drawable*): Unit = {
    val g = g2(image)
    drawables.foreach { d =>
      d.context.configure(g)
      d.draw(g)
    }
    g.dispose()
  }

  def draw(drawables: Drawable*): Canvas = draw(drawables)
  def draw(drawables: Iterable[Drawable]): Canvas = {
    val target = image.copy
    val g = g2(target)
    drawables.foreach { d =>
      d.context.configure(g)
      d.draw(g)
    }
    g.dispose()
    target
  }

  @deprecated("use WatermarkFilter", "2.1.0")
  def watermark(text: String): Canvas = watermark(text, 0.5)

  @deprecated("use WatermarkFilter", "2.1.0")
  def watermark(text: String, alpha: Double): Canvas = {
    image.filter(new WatermarkFilter(text, new java.awt.Font(java.awt.Font.SERIF, 0, 24), alpha))
  }
}

object Canvas {
  implicit def image2canvas(image: Image): Canvas = new Canvas(image)
  implicit def canvas2image(canvas: Canvas): Image = canvas.image
}

