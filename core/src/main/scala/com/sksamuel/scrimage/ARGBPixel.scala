package com.sksamuel.scrimage

import scala.language.implicitConversions

/** @author Stephen Samuel */
class ARGBPixel(argb: Int) extends Pixel {

  override def alpha: Int = argb >> 24 & 0xFF
  override def red: Int = argb >> 16 & 0xFF
  override def green: Int = argb >> 8 & 0xFF
  override def blue: Int = argb & 0xFF

  override def toARGBInt: Int = argb

  def hue: Float = java.awt.Color.RGBtoHSB(red, green, blue, null)(0)
  def saturation: Float = java.awt.Color.RGBtoHSB(red, green, blue, null)(1)
  def brightness: Float = java.awt.Color.RGBtoHSB(red, green, blue, null)(2)
}

object ARGBPixel {
  implicit def int2pixel(pixel: Int): ARGBPixel = new ARGBPixel(pixel)
  def apply(argb: Int): ARGBPixel = new ARGBPixel(argb)
  def apply(alpha: Int, r: Int, g: Int, b: Int): ARGBPixel = {
    val int = alpha << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF) << 0
    new ARGBPixel(int)
  }
}

class RGBPixel(rgb: Int) extends Pixel {

  override def alpha: Int = 0
  override def red: Int = rgb >> 16 & 0xFF
  override def green: Int = rgb >> 8 & 0xFF
  override def blue: Int = rgb & 0xFF

  def toARGBPixel: ARGBPixel = ARGBPixel(0, red, green, blue)
  override def toARGBInt: Int = toARGBPixel.toARGBInt
}

object RGBPixel {
  def apply(r: Int, g: Int, b: Int): RGBPixel = {
    val int = 0xFF << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF) << 0
    new RGBPixel(int)
  }
}

trait Pixel {

  /**
   * Returns the alpha component as an Int from 0 to 255
   */
  def alpha: Int

  /**
   * Returns the red component as an Int from 0 to 255
   */
  def red: Int

  /**
   * Returns the green component as an Int from 0 to 255
   */
  def green: Int

  /**
   * Returns the blue component as an Int from 0 to 255
   */
  def blue: Int

  /**
   * Returns an compacted int for ARGB
   */
  def toARGBInt: Int
}