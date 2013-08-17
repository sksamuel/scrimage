package com.sksamuel.scrimage

import java.awt.Color

/** @author Stephen Samuel */
class Pixel(pixel: Int) {

  def alpha: Int = pixel >> 24 & 0xFF

  def red: Int = pixel >> 16 & 0xFF
  def green: Int = pixel >> 8 & 0xFF
  def blue: Int = pixel & 0xFF

  def hue = Color.RGBtoHSB(red, green, blue, null)(0)
  def saturation = Color.RGBtoHSB(red, green, blue, null)(0)
  def brightness = Color.RGBtoHSB(red, green, blue, null)(0)
}

object Pixel {
  implicit def int2pixel(pixel: Int) = new Pixel(pixel)
}
