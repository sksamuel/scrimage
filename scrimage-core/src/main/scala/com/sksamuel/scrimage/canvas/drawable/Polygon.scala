package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Context, Drawable}

import scala.language.implicitConversions

case class Polygon(points: Seq[Point], configure: Graphics2D => Unit = g2 => ()) extends Drawable {
  def draw(g: Graphics2D): Unit = g.drawPolygon(points.map(_.x).toArray, points.map(_.y).toArray, points.size)
  def fill: FilledPolygon = FilledPolygon(points, configure)
  def open: Polyline = Polyline(points, configure)
}

object Polygon {

  implicit def awt2polygon(awt: java.awt.Polygon): Polygon = {
    val points = awt.xpoints.zip(awt.ypoints).map(Point.tuple2point)
    Polygon(points)
  }

  def rectangle(x: Int, y: Int, width: Int, height: Int): Polygon = {
    Polygon(Seq(x -> y, (x + width) -> y, (x + width) -> (y + height), x -> (y + height)))
  }

  @deprecated("Use variant that accepts function to customize Graphics2D instead of Context", "3.0.0")
  def apply(points: Seq[Point], context: Context): Polygon = Polygon(points, context.toFn)
}