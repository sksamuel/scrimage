package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Drawable, GraphicsContext}

case class Polyline(points: Seq[java.awt.Point], context: GraphicsContext) extends Drawable {
  def draw(g: Graphics2D): Unit = g.drawPolyline(points.map(_.x).toArray, points.map(_.y).toArray, points.size)
  def close: Polygon = Polygon(points, context)
}