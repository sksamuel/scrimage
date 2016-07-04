package com.sksamuel.scrimage.canvas.drawable

import java.awt.Graphics2D

import com.sksamuel.scrimage.canvas.{Context, Drawable}

case class Rect(x: Int, y: Int, width: Int, height: Int, configure: Graphics2D => Unit = g => ()) extends Drawable {

  def draw(g: Graphics2D): Unit = g.drawRect(x, y, width, height)

  def fill: FilledRect = FilledRect(x, y, width, height, configure)

  def rounded(arcWidth: Int, arcHeight: Int): RoundedRect =
    RoundedRect(x, y, width, height, arcWidth, arcHeight, configure)
}

object Rect {
  def apply(x: Int, y: Int, width: Int, height: Int, context: Context): Rect = Rect(x, y, width, height, context.toFn)
}