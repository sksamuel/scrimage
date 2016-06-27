package com.sksamuel.scrimage.canvas

import java.awt.Graphics2D

import com.sksamuel.scrimage.Image

import scala.language.implicitConversions
import java.awt.{Font => JFont}

trait Drawable {
  def draw(g: Graphics2D): Unit
  def configure: Graphics2D => Unit
}

trait ContextDrawable extends Drawable {
  def context: Context
  def configure = context.toConfigure
}

object Drawable {
  def apply(draw: Graphics2D => Unit, _context: Context = Context.Default): Drawable = new ContextDrawable {
    def draw(g: Graphics2D): Unit = draw(g)
    def context: Context = _context
  }
  def apply(img: Image, x: Int, y: Int): DrawableImage = DrawableImage(img, x, y)
  def apply(str: String, x: Int, y: Int): DrawableString = DrawableString(str, x, y)
}

case class Rect(x: Int, y: Int, width: Int, height: Int, context: Context = Context.Default) extends ContextDrawable {
  def draw(g: Graphics2D): Unit = g.drawRect(x, y, width, height)
  def fill: FilledRect = FilledRect(x, y, width, height, context)
  def rounded(arcWidth: Int, arcHeight: Int): RoundedRect = {
    RoundedRect(x, y, width, height, arcWidth, arcHeight, context)
  }
}

case class FilledRect(x: Int, y: Int, width: Int, height: Int, context: Context = Context.Default) extends ContextDrawable {

  def draw(g: Graphics2D): Unit = g.fillRect(x, y, width, height)
  def rounded(arcWidth: Int, arcHeight: Int): FilledRoundedRect = {
    FilledRoundedRect(x, y, width, height, arcWidth, arcHeight, context)
  }
}

case class RoundedRect(x: Int,
                       y: Int,
                       width: Int,
                       height: Int,
                       arcWidth: Int,
                       arcHeight: Int,
                       configure: Graphics2D => Unit = g => ()) extends Drawable {
  def draw(g: Graphics2D): Unit = g.drawRoundRect(x, y, width, height, arcWidth, arcHeight)
  def fill: FilledRoundedRect = FilledRoundedRect(x, y, width, height, arcWidth, arcHeight, configure)
}

object RoundedRect {
  @deprecated("Use variant that accepts function to customize Graphics2D instead of Context", "3.0.0")
  def apply(x: Int,
            y: Int,
            width: Int,
            height: Int,
            arcWidth: Int,
            arcHeight: Int,
            context: Context): RoundedRect = {
    RoundedRect(x, y, width, height, arcWidth, arcHeight, context.toConfigure)
  }
}

case class FilledRoundedRect(x: Int,
                             y: Int,
                             width: Int,
                             height: Int,
                             arcWidth: Int,
                             arcHeight: Int,
                             configure: Graphics2D => Unit = g => ()) extends Drawable {
  def draw(g: Graphics2D): Unit = g.fillRoundRect(x, y, width, height, arcWidth, arcHeight)
}

object FilledRoundedRect {
  @deprecated("Use variant that accepts function to customize Graphics2D instead of Context", "3.0.0")
  def apply(x: Int,
            y: Int,
            width: Int,
            height: Int,
            arcWidth: Int,
            arcHeight: Int,
            context: Context): FilledRoundedRect = {
    FilledRoundedRect(x, y, width, height, arcWidth, arcHeight, context.toConfigure)
  }
}

case class Line(x0: Int, y0: Int, x1: Int, y1: Int, context: Context = Context.Default) extends ContextDrawable {
  def draw(g: Graphics2D): Unit = g.drawLine(x0, y0, x1, y1)
}

case class Arc(x: Int,
               y: Int,
               width: Int,
               height: Int,
               startAngle: Int,
               endAngle: Int,
               context: Context = Context.Default) extends ContextDrawable {
  def draw(g: Graphics2D): Unit = g.drawArc(x, y, width, height, startAngle, endAngle)
  def fill: FilledArc = FilledArc(x, y, width, height, startAngle, endAngle, context)
}

case class FilledArc(x: Int,
                     y: Int,
                     width: Int,
                     height: Int,
                     startAngle: Int,
                     endAngle: Int,
                     context: Context = Context.Default) extends ContextDrawable {
  def draw(g: Graphics2D): Unit = g.fillArc(x, y, width, height, startAngle, endAngle)
}

case class Oval(x: Int, y: Int, width: Int, height: Int, context: Context = Context.Default) extends ContextDrawable {
  def draw(g: Graphics2D): Unit = g.drawOval(x, y, width, height)
  def fill: FilledOval = FilledOval(x, y, width, height, context)
}

case class FilledOval(x: Int, y: Int, width: Int, height: Int, context: Context = Context.Default) extends ContextDrawable {
  def draw(g: Graphics2D): Unit = g.fillOval(x, y, width, height)
}

case class Point(x: Int, y: Int, context: Context = Context.Default) extends ContextDrawable {
  def draw(g: Graphics2D): Unit = g.drawLine(x, y, x, y)
}

object Point {
  implicit def tuple2point(p: (Int, Int)): Point = Point(p._1, p._2)
}

case class Polygon(points: Seq[Point], context: Context = Context.Default) extends ContextDrawable {
  def draw(g: Graphics2D): Unit = g.drawPolygon(points.map(_.x).toArray, points.map(_.y).toArray, points.size)
  def fill: FilledPolygon = FilledPolygon(points, context)
  def open: Polyline = Polyline(points, context)
}

case class Polyline(points: Seq[Point], context: Context = Context.Default) extends ContextDrawable {
  def draw(g: Graphics2D): Unit = g.drawPolyline(points.map(_.x).toArray, points.map(_.y).toArray, points.size)
  def close: Polygon = Polygon(points, context)
}

case class FilledPolygon(points: Seq[Point], configure: Graphics2D => Unit) extends Drawable {
  def draw(g: Graphics2D): Unit = {
    g.fillPolygon(points.map(_.x).toArray, points.map(_.y).toArray, points.size)
  }
}

object FilledPolygon {
  def apply(points: Seq[Point], context: Context): FilledPolygon = FilledPolygon(points, context.configure(_))
}

object Polygon {

  implicit def awt2polygon(awt: java.awt.Polygon): Polygon = {
    val points = awt.xpoints.zip(awt.ypoints).map(Point.tuple2point)
    Polygon(points)
  }

  def rectangle(x: Int, y: Int, width: Int, height: Int): Polygon = {
    Polygon(Seq(x -> y, (x + width) -> y, (x + width) -> (y + height), x -> (y + height)))
  }
}

case class DrawableString(text: String, x: Int, y: Int, configure: Graphics2D => Unit = g2 => ()) extends Drawable {
  def draw(g2: Graphics2D): Unit = {
    configure(g2)
    g2.drawString(text, x, y)
  }
}

object DrawableString {
  @deprecated("Use variant that accepts function to customize Graphics2D instead of Context", "3.0.0")
  def apply(text: String, x: Int, y: Int, context: Context): DrawableString =
    DrawableString(text, x, y, g2 => context.configure(g2))
}

case class DrawableImage(imageToDraw: Image, x: Int, y: Int, context: Context = Context.Default) extends ContextDrawable {
  def draw(g: Graphics2D): Unit = g.drawImage(imageToDraw.awt, x, y, null)
}

