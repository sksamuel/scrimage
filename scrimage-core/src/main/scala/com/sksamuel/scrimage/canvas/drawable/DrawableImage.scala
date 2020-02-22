package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.canvas.{Drawable, GraphicsContext}

case class DrawableImage(imageToDraw: ImmutableImage, x: Int, y: Int, context: GraphicsContext) extends Drawable {
  def draw(g: Graphics2D): Unit = g.drawImage(imageToDraw.awt, x, y, null)
}
