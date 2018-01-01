package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Drawable, GraphicsContext}

import scala.language.implicitConversions

case class Point(x: Int, y: Int, context: GraphicsContext) extends Drawable {
  def draw(g: Graphics2D): Unit = g.drawLine(x, y, x, y)
}