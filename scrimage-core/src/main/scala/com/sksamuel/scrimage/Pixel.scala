package com.sksamuel.scrimage

import scala.language.implicitConversions

/**
 * A pixel is an encoding of a color value used in rasters.
 * The pixel is encoded using an ARGB packed int
 */
case class Pixel(argb: Int) {

  def alpha: Int = argb >> 24 & 0xFF
  def red: Int = argb >> 16 & 0xFF
  def green: Int = argb >> 8 & 0xFF
  def blue: Int = argb & 0xFF

  def toInt: Int = toARGBInt
  def toARGBInt: Int = argb
  def toColor: RGBColor = RGBColor(red, green, blue, alpha)

  def mapByComponent(f: Int => Int): Pixel = Pixel(f(red), f(green), f(blue), alpha)
}

object Pixel {

  implicit def int2pixel(pixel: Int): Pixel = new Pixel(pixel)

  def apply(r: Int, g: Int, b: Int, alpha: Int): Pixel = {
    val int = alpha << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF) << 0
    Pixel(int)
  }

  def apply(color: Color): Pixel = color.toPixel
}