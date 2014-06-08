package com.sksamuel.scrimage

/** @author Stephen Samuel */
class Pixel(pixel: Int) {

  def alpha: Int = pixel >> 24 & 0xFF

  def red: Int = pixel >> 16 & 0xFF
  def green: Int = pixel >> 8 & 0xFF
  def blue: Int = pixel & 0xFF

  def hue = java.awt.Color.RGBtoHSB(red, green, blue, null)(0)
  def saturation = java.awt.Color.RGBtoHSB(red, green, blue, null)(1)
  def brightness = java.awt.Color.RGBtoHSB(red, green, blue, null)(2)
}

object Pixel {
  implicit def int2pixel(pixel: Int) = new Pixel(pixel)
}
