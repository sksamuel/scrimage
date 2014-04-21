package com.sksamuel.scrimage

import java.awt.Color

/** @author Stephen Samuel */
trait Pixel {
  def alpha: Int
  def red: Int
  def green: Int
  def blue: Int
}

class ARGBPixel(pixel: Int) extends Pixel {

  def alpha: Int = pixel >> 24 & 0xFF
  def red: Int = pixel >> 16 & 0xFF
  def green: Int = pixel >> 8 & 0xFF
  def blue: Int = pixel & 0xFF

  def hue = Color.RGBtoHSB(red, green, blue, null)(0)
  def saturation = Color.RGBtoHSB(red, green, blue, null)(1)
  def brightness = Color.RGBtoHSB(red, green, blue, null)(2)
}

object ARGBPixel {
  implicit def int2pixel(pixel: Int) = new ARGBPixel(pixel)
  def bytesToPixel(red: Byte, green: Byte, blue: Byte, alpha: Byte): ARGBPixel = {
    new ARGBPixel(alpha << 24 & red << 16 & green << 8 & blue)
  }
}
