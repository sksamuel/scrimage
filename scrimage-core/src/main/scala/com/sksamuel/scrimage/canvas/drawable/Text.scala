package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Context, Drawable}

case class Text(text: String, x: Int, y: Int, configure: Graphics2D => Unit = g2 => ()) extends Drawable {
  def draw(g2: Graphics2D): Unit = {
    configure(g2)
    g2.drawString(text, x, y)
  }
}

@deprecated("Use Text", "3.0.0")
case class DrawableString(text: String, x: Int, y: Int, configure: Graphics2D => Unit = g2 => ()) extends Drawable {
  def draw(g2: Graphics2D): Unit = {
    configure(g2)
    g2.drawString(text, x, y)
  }
}

object DrawableString {
  @deprecated("Use Text", "3.0.0")
  def apply(text: String, x: Int, y: Int, context: Context): DrawableString = DrawableString(text, x, y, g2 => context.configure(g2))
}