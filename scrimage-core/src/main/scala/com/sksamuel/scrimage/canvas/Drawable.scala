package com.sksamuel.scrimage.canvas

import java.awt.Graphics2D

import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.canvas.drawable.{DrawableImage, Text}

import scala.language.implicitConversions

trait Drawable {
  def draw(g: Graphics2D): Unit
  def context: GraphicsContext
}

object Drawable {
  def apply(img: Image, x: Int, y: Int): DrawableImage = DrawableImage(img, x, y, GraphicsContext.identity)
  def apply(str: String, x: Int, y: Int): Text = Text(str, x, y, GraphicsContext.identity())
}



