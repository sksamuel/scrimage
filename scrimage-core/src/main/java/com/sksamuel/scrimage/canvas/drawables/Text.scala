package com.sksamuel.scrimage.canvas.drawables

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Drawable, GraphicsContext}

case class Text(text: String, x: Int, y: Int, context: GraphicsContext) extends Drawable {
  def draw(g2: Graphics2D): Unit = {
    context.configure(g2)
    g2.drawString(text, x, y)
  }
}

object DrawableString {
  @deprecated("Use Text", "3.0.0")
  def apply(text: String, x: Int, y: Int, context: GraphicsContext): Text = Text(text, x, y, context)
}