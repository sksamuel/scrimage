package com.sksamuel.scrimage.canvas

import java.awt.{BasicStroke, Graphics2D, RenderingHints}

import com.sksamuel.scrimage.{Color, Image}

import scala.language.implicitConversions

case class Canvas(image: Image) {

  private def g2(image: Image): Graphics2D = image.awt().getGraphics.asInstanceOf[Graphics2D]

  protected[scrimage] def drawInPlace(drawables: Drawable*): Unit = {
    val g = g2(image)
    drawables.foreach { d =>
      d.configure(g)
      d.draw(g)
    }
    g.dispose()
  }

  def draw(context: Context, drawables: Drawable*): Canvas = draw(context, drawables)
  def draw(context: Context, drawables: Iterable[Drawable]): Canvas = {
    val target = image.copy
    val g = g2(target)
    drawables.foreach { d =>
      d.configure(g)
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
      d.configure(g)
      d.draw(g)
    }
    g.dispose()
    new Canvas(target)
  }
}

object Canvas {
  implicit def image2canvas(image: Image): Canvas = new Canvas(image)
  implicit def canvas2image(canvas: Canvas): Image = canvas.image

  implicit class RichGraphics2(g2: Graphics2D) {

    def setBasicStroke(width: Float): Unit = g2.setStroke(new BasicStroke(width))

    def setWhite(): Unit = g2.setColor(Color.White)
    def setBlack(): Unit = g2.setColor(Color.Black)

    def setAntiAlias(aa: Boolean) = {
      if (aa) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
      } else {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF)
      }
    }
  }

}

