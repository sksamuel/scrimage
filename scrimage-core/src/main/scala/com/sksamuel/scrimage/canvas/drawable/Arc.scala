package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Drawable, GraphicsContext}

case class Arc(x: Int,
               y: Int,
               width: Int,
               height: Int,
               startAngle: Int,
               endAngle: Int,
               context: GraphicsContext) extends Drawable {
  def draw(g: Graphics2D): Unit = g.drawArc(x, y, width, height, startAngle, endAngle)
  def fill: FilledArc = FilledArc(x, y, width, height, startAngle, endAngle, context)
}