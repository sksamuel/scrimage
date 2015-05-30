package com.sksamuel.scrimage

import scala.language.implicitConversions

/**
 * A pixel is an encoding of a color value used in rasters.
 *
 * For example, a color may be encoded using a packed integer, with 4 components ARGB. This would be modelled
 * by the ARGBIntPixel.
 *
 * Another example is 4 bytes for ARGB components. This would be modelled by the ARGBBytePixel class.
 *
 * @author Stephen Samuel
 */
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

  def toARGBIntPixel: ARGBIntPixel = ARGBIntPixel(red, green, blue, alpha)

  /**
   * Returns an compacted int
   */
  def toARGBInt: Int = toARGBIntPixel.argb

  /**
   * Returns the colour this pixel represents as an instance of an RGBColor.
   */
  def toRGBColor: RGBColor = RGBColor(red, green, blue, alpha)
}

case class ARGBIntPixel(argb: Int) extends Pixel {

  override def alpha: Int = argb >> 24 & 0xFF
  override def red: Int = argb >> 16 & 0xFF
  override def green: Int = argb >> 8 & 0xFF
  override def blue: Int = argb & 0xFF

  override def toARGBInt: Int = argb
  override def toARGBIntPixel: ARGBIntPixel = this
}

object ARGBIntPixel {

  implicit def int2pixel(pixel: Int): ARGBIntPixel = new ARGBIntPixel(pixel)

  //  def apply(argb: Array[Int]): ARGBIntPixel = ARGBIntPixel(argb.head, argb(1), argb(2), argb(3))

  def apply(r: Int, g: Int, b: Int, alpha: Int): ARGBIntPixel = {
    val int = alpha << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF) << 0
    ARGBIntPixel(int)
  }
}

case class ABGRBytePixel(_alpha: Byte, _blue: Byte, _green: Byte, _red: Byte) extends Pixel {
  override def toARGBInt: Int = ARGBIntPixel(red, green, blue, alpha).toARGBInt
  def alpha: Int = _alpha
  def red: Int = _red
  def green: Int = _green
  def blue: Int = _blue
}