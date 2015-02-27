package com.sksamuel.scrimage.canvas

import java.awt.Graphics2D
import com.sksamuel.scrimage.{ Color, Image }

trait Drawable {
  def draw(g: Graphics2D): Unit
  def withPainter(color: Color) = ColoredDrawable(color, this)
  def withPainter(painter: Painter) = ColoredDrawable(painter, this)
}

object Drawable {
  def apply(draw: Graphics2D => Unit): Drawable = new Drawable {
    def draw(g: Graphics2D) = draw(g)
  }
  def apply(img: Image, x: Int, y: Int) = DrawableImage(img, x, y)
  def apply(str: String, x: Int, y: Int) = DrawableString(str, x, y)
}

case class ColoredDrawable(painter: Painter, drawable: Drawable) extends Drawable {
  def draw(g: Graphics2D) = {
    val previousPaint = g.getPaint
    g.setPaint(painter.paint)
    drawable.draw(g)
    g.setPaint(previousPaint)
  }
}

case class Rect(x: Int, y: Int, width: Int, height: Int) extends Drawable {
  def draw(g: Graphics2D) = g.drawRect(x, y, width, height)
  def fill = FilledRect(x, y, width, height)
  def rounded(arcWidth: Int, arcHeight: Int) =
    RoundedRect(x, y, width, height, arcWidth, arcHeight)
}

case class FilledRect(x: Int, y: Int, width: Int, height: Int) extends Drawable {
  def draw(g: Graphics2D) = g.fillRect(x, y, width, height)
  def rounded(arcWidth: Int, arcHeight: Int) =
    FilledRoundedRect(x, y, width, height, arcWidth, arcHeight)
}

case class RoundedRect(x: Int, y: Int, width: Int, height: Int,
                       arcWidth: Int, arcHeight: Int) extends Drawable {
  def draw(g: Graphics2D) = g.drawRoundRect(x, y, width, height, arcWidth, arcHeight)
  def fill = FilledRoundedRect(x, y, width, height, arcWidth, arcHeight)
}

case class FilledRoundedRect(x: Int, y: Int, width: Int, height: Int,
                             arcWidth: Int, arcHeight: Int) extends Drawable {
  def draw(g: Graphics2D) = g.fillRoundRect(x, y, width, height, arcWidth, arcHeight)
}

case class Line(x0: Int, y0: Int, x1: Int, y1: Int) extends Drawable {
  def draw(g: Graphics2D) = g.drawLine(x0, y0, x1, y1)
}

case class Arc(x: Int, y: Int, width: Int, height: Int,
               startAngle: Int, endAngle: Int) extends Drawable {
  def draw(g: Graphics2D) = g.drawArc(x, y, width, height, startAngle, endAngle)
  def fill = FilledArc(x, y, width, height, startAngle, endAngle)
}

case class FilledArc(x: Int, y: Int, width: Int, height: Int,
                     startAngle: Int, endAngle: Int) extends Drawable {
  def draw(g: Graphics2D) = g.fillArc(x, y, width, height, startAngle, endAngle)
}

case class Oval(x: Int, y: Int, width: Int, height: Int) extends Drawable {
  def draw(g: Graphics2D) = g.drawOval(x, y, width, height)
  def fill = FilledOval(x, y, width, height)
}

case class FilledOval(x: Int, y: Int, width: Int, height: Int) extends Drawable {
  def draw(g: Graphics2D) = g.fillOval(x, y, width, height)
}

case class Point(x: Int, y: Int) extends Drawable {
  def draw(g: Graphics2D) = g.drawLine(x, y, x, y)
}

object Point {
  implicit def tuple2point(p: (Int, Int)): Point = Point(p._1, p._2)
}

case class Polygon(points: Seq[Point]) extends Drawable {
  def draw(g: Graphics2D) = g.drawPolygon(points.map(_.x).toArray,
    points.map(_.y).toArray,
    points.size)
  def fill = FilledPolygon(points)
  def open = Polyline(points)
}

case class Polyline(points: Seq[Point]) extends Drawable {
  def draw(g: Graphics2D) = g.drawPolyline(points.map(_.x).toArray,
    points.map(_.y).toArray,
    points.size)
  def close = Polygon(points)
}

case class FilledPolygon(points: Seq[Point]) extends Drawable {
  def draw(g: Graphics2D) = g.fillPolygon(points.map(_.x).toArray,
    points.map(_.y).toArray,
    points.size)
}

object Polygon {

  implicit def awt2polygon(awt: java.awt.Polygon): Polygon = {
    val points = awt.xpoints.zip(awt.ypoints).map(Point.tuple2point)
    Polygon(points)
  }

  def rectangle(x: Int, y: Int, width: Int, height: Int) = {
    Polygon(Seq(x -> y, (x + width) -> y, (x + width) -> (y + height), x -> (y + height)))
  }
}

case class DrawableString(text: String, x: Int, y: Int) extends Drawable {
  def draw(g: Graphics2D) = g.drawString(text, x, y)
}

case class DrawableImage(imageToDraw: Image, x: Int, y: Int) extends Drawable {
  def draw(g: Graphics2D) = g.drawImage(imageToDraw.awt, x, y, null)
}

