package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Context, Drawable}

case class FilledRect(x: Int, y: Int, width: Int, height: Int, configure: Graphics2D => Unit = g2 => ()) extends Drawable {

  def draw(g: Graphics2D): Unit = g.fillRect(x, y, width, height)

  def rounded(arcWidth: Int, arcHeight: Int): FilledRoundedRect = {
    FilledRoundedRect(x, y, width, height, arcWidth, arcHeight, configure)
  }

  def outline: Rect = Rect(x, y, width, height, configure)
}

object FilledRect {
  @deprecated("Use variant that accepts function to customize Graphics2D instead of Context", "3.0.0")
  def apply(x: Int, y: Int, width: Int, height: Int, context: Context): FilledRect = {
    FilledRect(x, y, width, height, context.toFn)
  }
}
