package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Context, Drawable}

case class Oval(x: Int, y: Int, width: Int, height: Int, configure: Graphics2D => Unit = g2 => ()) extends Drawable {
  def draw(g: Graphics2D): Unit = g.drawOval(x, y, width, height)
  def fill: FilledOval = FilledOval(x, y, width, height, configure)
}

object Oval {
  @deprecated("Use variant that accepts function to customize Graphics2D instead of Context", "3.0.0")
  def apply(x: Int, y: Int, width: Int, height: Int, context: Context): Oval = Oval(x, y, width, height, context.toFn)
}
