package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Context, Drawable}

case class Arc(x: Int,
               y: Int,
               width: Int,
               height: Int,
               startAngle: Int,
               endAngle: Int,
               configure: Graphics2D => Unit = g2 => ()) extends Drawable {
  def draw(g: Graphics2D): Unit = g.drawArc(x, y, width, height, startAngle, endAngle)
  def fill: FilledArc = FilledArc(x, y, width, height, startAngle, endAngle, configure)
}

object Arc {
  @deprecated("Use variant that accepts function to customize Graphics2D instead of Context", "3.0.0")
  def apply(x: Int,
            y: Int,
            width: Int,
            height: Int,
            startAngle: Int,
            endAngle: Int,
            context: Context): Arc = Arc(x, y, width, height, startAngle, endAngle)
}