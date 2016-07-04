package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Context, Drawable}

case class Polyline(points: Seq[Point], configure: Graphics2D => Unit = g2 => ()) extends Drawable {
  def draw(g: Graphics2D): Unit = g.drawPolyline(points.map(_.x).toArray, points.map(_.y).toArray, points.size)
  def close: Polygon = Polygon(points, configure)
}

object Polyline {
  def apply(points: Seq[Point], context: Context): Polyline = Polyline(points, context.toFn)
}
