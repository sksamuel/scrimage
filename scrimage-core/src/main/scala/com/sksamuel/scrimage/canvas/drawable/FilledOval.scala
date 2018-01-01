package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Drawable, GraphicsContext}

case class FilledOval(x: Int, y: Int, width: Int, height: Int, context: GraphicsContext) extends Drawable {
  def draw(g: Graphics2D): Unit = g.fillOval(x, y, width, height)
  def outline: Oval = Oval(x, y, width, height, context)
}