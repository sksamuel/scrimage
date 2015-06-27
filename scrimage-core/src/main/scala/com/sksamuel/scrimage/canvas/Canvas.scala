package com.sksamuel.scrimage.canvas

import java.awt.Graphics2D

import com.sksamuel.scrimage.{Color, Image}
import com.sksamuel.scrimage.canvas.Context

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

  def draw(context: Context, drawables: Drawable*): Canvas = draw(context, drawables)
  def draw(context: Context, drawables: Iterable[Drawable]): Canvas = {
    val target = image.copy
    val g = g2(target)
    drawables.foreach { d =>
      d.context.configure(g)
      context.configure(g)
      d.draw(g)
    }
    g.dispose()
    new Canvas(target)
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
    new Canvas(target)
  }

  @deprecated("use WatermarkFilter", "2.1.0")
  def watermark(text: String): Canvas = {
    image.filter(new WatermarkStampFilter(text, 12, new java.awt.Font(java.awt.Font.SERIF, 0, 24), 0.5))
  }

  @deprecated("use WatermarkFilter", "2.1.0")
  def watermark(text: String, alpha: Double): Canvas = {
    image.filter(new WatermarkStampFilter(text, 12, new java.awt.Font(java.awt.Font.SERIF, 0, 24), alpha))
  }
}

object Canvas {
  implicit def image2canvas(image: Image): Canvas = new Canvas(image)
  implicit def canvas2image(canvas: Canvas): Image = canvas.image
}

