package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Drawable, GraphicsContext}

case class FilledRect(x: Int,
                      y: Int,
                      width: Int,
                      height: Int,
                      context: GraphicsContext) extends Drawable {

  def draw(g: Graphics2D): Unit = g.fillRect(x, y, width, height)

  def rounded(arcWidth: Int, arcHeight: Int): FilledRoundedRect = {
    FilledRoundedRect(x, y, width, height, arcWidth, arcHeight, context)
  }

  def outline: Rect = Rect(x, y, width, height, context)
}