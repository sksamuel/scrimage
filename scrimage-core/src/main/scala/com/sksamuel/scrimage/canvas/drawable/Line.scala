package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Context, Drawable}

case class Line(x0: Int, y0: Int, x1: Int, y1: Int, configure: Graphics2D => Unit = g2 => ()) extends Drawable {
  def draw(g: Graphics2D): Unit = g.drawLine(x0, y0, x1, y1)
}

object Line {
  @deprecated("Use variant that accepts function to customize Graphics2D instead of Context", "3.0.0")
  def apply(x0: Int, y0: Int, x1: Int, y1: Int, context: Context): Line = Line(x0, y0, x1, y1, context.toFn)
}