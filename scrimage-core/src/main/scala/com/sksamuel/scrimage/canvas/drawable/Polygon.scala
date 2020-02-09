package com.sksamuel.scrimage.canvas.drawable

import com.sksamuel.scrimage.canvas.{Drawable, GraphicsContext}
import java.awt.Graphics2D
import scala.language.implicitConversions

case class Polygon(points: Seq[java.awt.Point], context: GraphicsContext) extends Drawable {
  def draw(g: Graphics2D): Unit = g.drawPolygon(points.map(_.x).toArray, points.map(_.y).toArray, points.size)
  def fill: FilledPolygon = FilledPolygon(points, context)
  def open: Polyline = Polyline(points, context)
}

object Polygon {

  implicit def awt2polygon(awt: java.awt.Polygon, context: GraphicsContext): Polygon = {
    val points = awt.xpoints.zip(awt.ypoints).map { case (x, y) => new java.awt.Point(x, y) }.toIndexedSeq
    Polygon(points, context)
  }

  def rectangle(x: Int, y: Int, width: Int, height: Int, context: GraphicsContext): Polygon = {
    Polygon(
      Seq(
        new java.awt.Point(x, y),
        new java.awt.Point(x + width, y),
        new java.awt.Point(x + width, y + height),
        new java.awt.Point(x, y + height)
      ),
      context
    )
  }
}