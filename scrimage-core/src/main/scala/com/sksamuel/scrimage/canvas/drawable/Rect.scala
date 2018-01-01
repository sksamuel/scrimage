package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Drawable, GraphicsContext}

case class Rect(x: Int, y: Int, width: Int, height: Int, context: GraphicsContext) extends Drawable {

  def draw(g: Graphics2D): Unit = g.drawRect(x, y, width, height)

  def fill: FilledRect = FilledRect(x, y, width, height, context)

  def rounded(arcWidth: Int, arcHeight: Int): RoundedRect =
    RoundedRect(x, y, width, height, arcWidth, arcHeight, context)
}