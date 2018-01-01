package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Drawable, GraphicsContext}

case class Line(x0: Int, y0: Int, x1: Int, y1: Int, context: GraphicsContext) extends Drawable {
  def draw(g: Graphics2D): Unit = g.drawLine(x0, y0, x1, y1)
}