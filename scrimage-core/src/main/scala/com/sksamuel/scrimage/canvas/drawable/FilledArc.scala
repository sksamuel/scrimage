package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Context, Drawable}

case class FilledArc(x: Int,
                     y: Int,
                     width: Int,
                     height: Int,
                     startAngle: Int,
                     endAngle: Int,
                     configure: Graphics2D => Unit = g2 => ()) extends Drawable {
  def draw(g: Graphics2D): Unit = g.fillArc(x, y, width, height, startAngle, endAngle)
  def outline = Arc(x, y, width, height, startAngle, endAngle, configure)
}

object FilledArc {
  @deprecated("Use variant that accepts function to customize Graphics2D instead of Context", "3.0.0")
  def apply(x: Int,
            y: Int,
            width: Int,
            height: Int,
            startAngle: Int,
            endAngle: Int,
            context: Context): FilledArc = FilledArc(x, y, width, height, startAngle, endAngle, context.toFn)
}