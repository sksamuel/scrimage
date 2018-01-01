package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Drawable, GraphicsContext}

case class RoundedRect(x: Int,
                       y: Int,
                       width: Int,
                       height: Int,
                       arcWidth: Int,
                       arcHeight: Int,
                       context: GraphicsContext) extends Drawable {

  def draw(g: Graphics2D): Unit = g.drawRoundRect(x, y, width, height, arcWidth, arcHeight)

  def fill: FilledRoundedRect = FilledRoundedRect(x, y, width, height, arcWidth, arcHeight, context)

  def squared = Rect(x, y, width, height, context)
}