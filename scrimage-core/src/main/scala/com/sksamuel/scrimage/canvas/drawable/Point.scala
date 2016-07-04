package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Context, Drawable}

import scala.language.implicitConversions

case class Point(x: Int, y: Int, configure: Graphics2D => Unit = g2 => ()) extends Drawable {
  def draw(g: Graphics2D): Unit = g.drawLine(x, y, x, y)
}

object Point {
  @deprecated("Use variant that accepts function to customize Graphics2D instead of Context", "3.0.0")
  def apply(x: Int, y: Int, context: Context): Point = Point(x, y, context.toFn)
  implicit def tuple2point(p: (Int, Int)): Point = Point(p._1, p._2)
}