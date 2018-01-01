package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Drawable, GraphicsContext}

case class Oval(x: Int, y: Int, width: Int, height: Int, context: GraphicsContext) extends Drawable {
  def draw(g: Graphics2D): Unit = g.drawOval(x, y, width, height)
  def fill: FilledOval = FilledOval(x, y, width, height, context)
}