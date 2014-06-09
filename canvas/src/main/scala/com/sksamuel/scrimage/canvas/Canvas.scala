package com.sksamuel.scrimage.canvas

import com.sksamuel.scrimage.{Color, X11Colorlist, Image}
import java.awt.{RenderingHints, Font, Graphics2D}

/** @author Stephen Samuel */
case class Canvas(image: Image,
                  painter: Painter = X11Colorlist.Black,
                  font: Font = new Font(Font.SERIF, 0, 24),
                  aliasing: Boolean = false) {

  def withPainter(painter: Painter): Canvas = copy(painter = painter)
  def withPainter(color: Color): Canvas = copy(painter = color)
  def withFont(font: Font): Canvas = copy(font = font)
  def withAliasing(aliasing: Boolean) = copy(aliasing = aliasing)

  private def g2(image: Image): Graphics2D = {
    val g2 = image.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setPaint(painter.paint)
    if (aliasing)
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g2.setFont(font)
    g2
  }

  def fill: Canvas = {
    val copy = image.copy
    val g = g2(copy)
    g.fillRect(0, 0, copy.width, copy.height)
    g.dispose()
    this.copy(image = copy)
  }

  def drawLine(x1: Int, y1: Int, x2: Int, y2: Int): Canvas = {
    val copy = image.copy
    val g = g2(copy)
    g.drawLine(x1, y1, x2, y2)
    g.dispose()
    this.copy(image = copy)
  }

  def drawImage(x: Int, y: Int, imageToDraw: Image): Canvas = {
    val copy = image.copy
    val g = g2(copy)
    g.drawImage(imageToDraw.awt, x, y, null)
    g.dispose()
    this.copy(image = copy)
  }

  def fillRect(x: Int, y: Int, w: Int, h: Int): Canvas = fillPoly(Polygon.rectange(x, y, w, h))
  def fillPoly(polygon: Polygon): Canvas = {
    val copy = image.copy
    val g = g2(copy)
    g.fillPolygon(polygon.points.map(_.x).toArray, polygon.points.map(_.y).toArray, polygon.points.size)
    g.dispose()
    this.copy(image = copy)
  }

  def drawRoundedRect(x: Int,
                      y: Int,
                      width: Int,
                      height: Int,
                      arcWidth: Int,
                      arcHeight: Int): Canvas = {
    val copy = image.copy
    val g = g2(copy)
    g.drawRoundRect(x, y, width, height, arcWidth, arcHeight)
    g.dispose()
    this.copy(image = copy)
  }

  def fillRoundedRect(x: Int, y: Int, width: Int, height: Int, arcWidth: Int, arcHeight: Int): Canvas = {
    val copy = image.copy
    val g = g2(copy)
    g.fillRoundRect(x, y, width, height, arcWidth, arcHeight)
    g.dispose()
    this.copy(image = copy)
  }

  def drawOval(x: Int, y: Int, width: Int, height: Int): Canvas = {
    val copy = image.copy
    val g = g2(copy)
    g.drawOval(x, y, width, height)
    g.dispose()
    this.copy(image = copy)
  }

  def fillOval(x: Int, y: Int, width: Int, height: Int): Canvas = {
    val copy = image.copy
    val g = g2(copy)
    g.fillOval(x, y, width, height)
    g.dispose()
    this.copy(image = copy)
  }

  def drawRect(x: Int, y: Int, w: Int, h: Int): Canvas = drawPoly(Polygon.rectange(x, y, w, h))
  def drawPoly(polygon: Polygon): Canvas = {
    val copy = image.copy
    val g = g2(copy)
    g.drawPolygon(polygon.points.map(_.x).toArray, polygon.points.map(_.y).toArray, polygon.points.size)
    g.dispose()
    this.copy(image = copy)
  }

  def drawString(x: Int, y: Int, text: String): Canvas = {
    val copy = image.copy
    val g = g2(copy)
    g.drawString(text, x, y)
    g.dispose()
    this.copy(image = copy)
  }

  def watermark(text: String): Canvas = watermark(text, 0.5)
  def watermark(text: String, alpha: Double): Canvas = image.filter(new Watermark(text, font, alpha))
}

object Canvas {
  implicit def image2canvas(image: Image): Canvas = new Canvas(image)
  implicit def canvas2image(canvas: Canvas): Image = canvas.image
}

case class Point(x: Int, y: Int)

object Point {
  implicit def tuple2point(p: (Int, Int)): Point = Point(p._1, p._2)
}

case class Polygon(points: Seq[Point])
object Polygon {
  def rectange(x: Int, y: Int, width: Int, height: Int) = {
    Polygon(Seq(x -> y, (x + width) -> y, (x + width) -> (y + height), x -> (y + height)))
  }
}