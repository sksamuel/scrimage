package com.sksamuel.scrimage.canvas.drawables

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Drawable, GraphicsContext, RichGraphics2D}

case class Line(x0: Int, y0: Int, x1: Int, y1: Int, context: GraphicsContext) extends Drawable {
  def draw(g: RichGraphics2D): Unit = g.drawLine(x0, y0, x1, y1)
}