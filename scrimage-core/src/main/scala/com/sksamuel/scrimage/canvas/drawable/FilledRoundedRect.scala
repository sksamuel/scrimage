package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Drawable, GraphicsContext}

case class FilledRoundedRect(x: Int,
                             y: Int,
                             width: Int,
                             height: Int,
                             arcWidth: Int,
                             arcHeight: Int,
                             context: GraphicsContext) extends Drawable {
  def draw(g: Graphics2D): Unit = g.fillRoundRect(x, y, width, height, arcWidth, arcHeight)
  def outline: RoundedRect = RoundedRect(x, y, width, height, arcWidth, arcHeight, context)
  def squared: FilledRect = FilledRect(x, y, width, height, context)
}