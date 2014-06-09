package com.sksamuel.scrimage.canvas

import com.sksamuel.scrimage.{X11Colorlist, Image}
import java.awt.{Font, Point, Graphics2D}

/** @author Stephen Samuel */
class Canvas(val image: Image, val painter: Painter = X11Colorlist.Black) {

  def withPainter(painter: Painter): Canvas = new Canvas(image, painter)

  def fill: Canvas = {
    val copy = image.copy
    val g2 = copy.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setPaint(painter.paint)
    g2.fillRect(0, 0, copy.width, copy.height)
    g2.dispose()
    new Canvas(copy, painter)
  }

  def drawLine(x1: Int, y1: Int, x2: Int, y2: Int): Canvas = {
    val copy = image.copy
    val g2 = copy.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setPaint(painter.paint)
    g2.drawLine(x1, y1, x2, y2)
    g2.dispose()
    new Canvas(copy, painter)
  }

  def drawRect(x: Int, y: Int, w: Int, h: Int): Canvas = {
    val copy = image.copy
    val g2 = copy.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setPaint(painter.paint)
    g2.drawRect(x, y, w, h)
    g2.dispose()
    new Canvas(copy, painter)
  }

  def fillRect(x: Int, y: Int, w: Int, h: Int): Canvas = {
    val copy = image.copy
    val g2 = copy.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setPaint(painter.paint)
    g2.fillRect(x, y, w, h)
    g2.dispose()
    new Canvas(copy, painter)
  }

  def fillPoly(points: Seq[Point]): Canvas = {
    val copy = image.copy
    val g2 = copy.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setPaint(painter.paint)
    g2.fillPolygon(points.map(_.x).toArray, points.map(_.y).toArray, points.size)
    g2.dispose()
    new Canvas(copy, painter)
  }

  def drawRoundedRect(x: Int,
                      y: Int,
                      width: Int,
                      height: Int,
                      arcWidth: Int,
                      arcHeight: Int): Canvas = {
    val copy = image.copy
    val g2 = copy.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setPaint(painter.paint)
    g2.drawRoundRect(x, y, width, height, arcWidth, arcHeight)
    g2.dispose()
    new Canvas(copy, painter)
  }

  def fillRoundedRect(x: Int, y: Int, width: Int, height: Int, arcWidth: Int, arcHeight: Int): Canvas = {
    val copy = image.copy
    val g2 = copy.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setPaint(painter.paint)
    g2.fillRoundRect(x, y, width, height, arcWidth, arcHeight)
    g2.dispose()
    new Canvas(copy, painter)
  }

  def drawOval(x: Int, y: Int, width: Int, height: Int): Canvas = {
    val copy = image.copy
    val g2 = copy.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setPaint(painter.paint)
    g2.drawOval(x, y, width, height)
    g2.dispose()
    new Canvas(copy, painter)
  }

  def fillOval(x: Int, y: Int, width: Int, height: Int): Canvas = {
    val copy = image.copy
    val g2 = copy.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setPaint(painter.paint)
    g2.fillOval(x, y, width, height)
    g2.dispose()
    new Canvas(copy, painter)
  }

  def drawPoly(points: Seq[Point]): Canvas = {
    val copy = image.copy
    val g2 = copy.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setPaint(painter.paint)
    g2.drawPolygon(points.map(_.x).toArray, points.map(_.y).toArray, points.size)
    g2.dispose()
    new Canvas(copy, painter)
  }

  def drawString(font: Font, x: Int, y: Int, text: String): Canvas = {
    val copy = image.copy
    val g2 = copy.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setPaint(painter.paint)
    g2.setFont(font)
    g2.drawString(text, x, y)
    g2.dispose()
    new Canvas(copy, painter)
  }

  def watermark(text: String): Canvas = watermark(text, 24, 0.5)
  def watermark(text: String, fontSize: Int, alpha: Double): Canvas = image.filter(new Watermark(text, fontSize, alpha))
}

object Canvas {
  implicit def image2canvas(image: Image): Canvas = new Canvas(image)
  implicit def canvas2image(canvas: Canvas): Image = canvas.image
}