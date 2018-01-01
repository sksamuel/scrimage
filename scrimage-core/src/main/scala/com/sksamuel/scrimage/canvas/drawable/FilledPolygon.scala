package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Drawable, GraphicsContext}

case class FilledPolygon(points: Seq[java.awt.Point], context: GraphicsContext) extends Drawable {
  def draw(g: Graphics2D): Unit = {
    g.fillPolygon(points.map(_.x).toArray, points.map(_.y).toArray, points.size)
  }
  def open: Polygon = Polygon(points, context)
}