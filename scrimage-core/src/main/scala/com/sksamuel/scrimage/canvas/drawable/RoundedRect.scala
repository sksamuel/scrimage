package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Context, Drawable}

case class RoundedRect(x: Int,
                       y: Int,
                       width: Int,
                       height: Int,
                       arcWidth: Int,
                       arcHeight: Int,
                       configure: Graphics2D => Unit = g => ()) extends Drawable {
  def draw(g: Graphics2D): Unit = g.drawRoundRect(x, y, width, height, arcWidth, arcHeight)
  def fill: FilledRoundedRect = FilledRoundedRect(x, y, width, height, arcWidth, arcHeight, configure)
}

object RoundedRect {
  @deprecated("Use variant that accepts function to customize Graphics2D instead of Context", "3.0.0")
  def apply(x: Int,
            y: Int,
            width: Int,
            height: Int,
            arcWidth: Int,
            arcHeight: Int,
            context: Context): RoundedRect = {
    RoundedRect(x, y, width, height, arcWidth, arcHeight, context.toFn)
  }
}