package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.canvas.{Context, ContextDrawable}

case class DrawableImage(imageToDraw: Image, x: Int, y: Int, context: Context = Context.Default) extends ContextDrawable {
  def draw(g: Graphics2D): Unit = g.drawImage(imageToDraw.awt, x, y, null)
}
