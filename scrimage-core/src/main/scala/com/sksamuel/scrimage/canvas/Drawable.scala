package com.sksamuel.scrimage.canvas

import java.awt.Graphics2D

import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.canvas.drawable.{DrawableImage, Text}

import scala.language.implicitConversions

trait Drawable {
  def draw(g: Graphics2D): Unit
  def configure: Graphics2D => Unit
}

trait ContextDrawable extends Drawable {
  def context: Context
  def configure: Graphics2D => Unit = context.toFn
}

object Drawable {
  def apply(draw: Graphics2D => Unit, _context: Context = Context.Default): Drawable = new ContextDrawable {
    def draw(g: Graphics2D): Unit = draw(g)
    def context: Context = _context
  }
  def apply(img: Image, x: Int, y: Int): DrawableImage = DrawableImage(img, x, y)
  def apply(str: String, x: Int, y: Int): Text = Text(str, x, y)
}



