package com.sksamuel.scrimage.canvas

import com.sksamuel.scrimage.{Color, Image}
import java.awt.{BasicStroke, Graphics2D, RenderingHints}
import scala.language.implicitConversions

case class Canvas(image: Image) {

  private def g2(image: Image): Graphics2D = image.awt().getGraphics.asInstanceOf[Graphics2D]

  protected[scrimage] def drawInPlace(drawable: Drawable): Unit = drawInPlace(Seq(drawable))
  protected[scrimage] def drawInPlace(drawables: Drawable*): Unit = drawInPlace(drawables)
  protected[scrimage] def drawInPlace(drawables: Iterable[Drawable]): Unit = {
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
    new Canvas(target)
  }
}

object Canvas {

  implicit def image2canvas(image: Image): Canvas = new Canvas(image)
  implicit def canvas2image(canvas: Canvas): Image = canvas.image

  implicit class RichGraphics2(g2: Graphics2D) {

    def setBasicStroke(width: Float): Unit = g2.setStroke(new BasicStroke(width))

    def setWhite(): Unit = g2.setColor(Color.White.toAWT)
    def setBlack(): Unit = g2.setColor(Color.Black.toAWT)

    def setAntiAlias(aa: Boolean): Unit = {
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

