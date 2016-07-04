package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Context, Drawable}

case class FilledPolygon(points: Seq[Point], configure: Graphics2D => Unit = g2 => ()) extends Drawable {
  def draw(g: Graphics2D): Unit = {
    g.fillPolygon(points.map(_.x).toArray, points.map(_.y).toArray, points.size)
  }
  def open: Polygon = Polygon(points, configure)
}

object FilledPolygon {
  @deprecated("Use variant that accepts function to customize Graphics2D instead of Context", "3.0.0")
  def apply(points: Seq[Point], context: Context): FilledPolygon = FilledPolygon(points, context.toFn)
}