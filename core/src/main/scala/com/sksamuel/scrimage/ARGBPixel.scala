package com.sksamuel.scrimage

import scala.language.implicitConversions

/** @author Stephen Samuel */
class ARGBPixel(pixel: Int) {

  def alpha: Int = pixel >> 24 & 0xFF

  def red: Int = pixel >> 16 & 0xFF
  def green: Int = pixel >> 8 & 0xFF
  def blue: Int = pixel & 0xFF

  def hue: Float = java.awt.Color.RGBtoHSB(red, green, blue, null)(0)
  def saturation: Float = java.awt.Color.RGBtoHSB(red, green, blue, null)(1)
  def brightness: Float = java.awt.Color.RGBtoHSB(red, green, blue, null)(2)
}

object ARGBPixel {
  implicit def int2pixel(pixel: Int) = new ARGBPixel(pixel)
}
