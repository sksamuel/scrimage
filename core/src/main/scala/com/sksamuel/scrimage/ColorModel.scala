package com.sksamuel.scrimage

/** Describes how to read Color from raw data.
  * The ColorModel may use several channels to encode the color,
  * and must provide a way to access to each of this channel.
  */
trait ColorModel {

  /** The data type that contains pixel information in this color model.
    * For example, if you were implementing an ARGB color model, you may chose to use ints
    * and pack the 4 components into an int using bit shifting, or you may choose to use 4 bytes.
    */
  type ChannelType

  /** The number of channels used by this ColorModel
    */
  val n_channel: Int

  def readChannel(pixel: Array[ChannelType],  offset: Int = 0)(channel: Int): Int

  /** Converts a pixel to an ARGB Int. */
  def readARGB(pixel: Array[ChannelType], offset: Int = 0): Int

  /** Converts an ARGB Int to a Pixel. */
  def writeARGB(pixel: Array[ChannelType], offset: Int = 0)(argb: Int): Unit

  /** Converts a pixel to a [[com.sksamuel.scrimage.Color]]. */
  def readColor(pixel: Array[ChannelType], offset: Int = 0): Color

  /** Converts a [[com.sksamuel.scrimage.Color]] to a Pixel. */
  def writeColor(pixel: Array[ChannelType], offset: Int = 0)(color: Color): Unit

  /** Creates an array of Pixel of the given size. */
  def newDataModel(width: Int, height: Int): Array[ChannelType]
}

/** A ColorModel that where channels value ranges between 0 and 255 and are store in one Byte*/
trait ByteColorModel extends ColorModel {
  type ChannelType = Byte
  def readChannel(pixel: Array[ChannelType], off: Int = 0)(channel: Int) =
    pixel(off + channel) & 0xff
  def newDataModel(width: Int, height: Int) = Array.ofDim[Byte](width * height * n_channel)
}


trait ARGBColorModel extends ByteColorModel {
  val n_channel = 4
  def readARGB(pixel: Array[ChannelType], off: Int = 0): Int = (
    readChannel(pixel, off)(0) << 24 |
    readChannel(pixel, off)(1) << 16 |
    readChannel(pixel, off)(2) <<  8 |
    readChannel(pixel, off)(3) )
  )
  def writeARGB(pixel: Array[ChannelType], off: Int = 0)(argb: Int): PixelType = {
    pixel(off) = (argb >> 24).toByte
    pixel(off + 1) = (argb >> 16).toByte
    pixel(off + 2) = (argb >> 8).toByte
    pixel(off + 3) = argb.toByte
  }
  def readColor(pixel: Array[ChannelType], off: Int = 0): Color =
    Color(pixel(off + 1), pixel(off + 2), pixel(off + 3), pixel(off + 0))

  def writeColor(pixel: Array[ChannelType], off: Int = 0)(color: Color) = {
    val c = color.toRGB
    pixel(off) = c.alpha.toByte
    pixel(off + 1) = c.red.toByte
    pixel(off + 2) = c.green.toByte
    pixel(off + 3) = c.blue.toByte
  }
}

trait RGBColorModel extends ByteColorModel {
  val n_channel = 3
  def readARGB(pixel: Array[ChannelType], off: Int = 0): Int = (
    0xff000000 |
    readChannel(pixel, off)(0) << 16 |
    readChannel(pixel, off)(1) <<  8 |
    readChannel(pixel, off)(2) )
  )
  def writeARGB(pixel: Array[ChannelType], off: Int = 0)(argb: Int): PixelType = {
    pixel(off + 0) = (argb >> 16).toByte
    pixel(off + 1) = (argb >> 8).toByte
    pixel(off + 2) = argb.toByte
  }
  def readColor(pixel: Array[ChannelType], off: Int = 0): Color =
    Color(pixel(off + 1), pixel(off + 2), pixel(off + 3))

  def writeColor(pixel: Array[ChannelType], off: Int = 0)(color: Color) = {
    val c = color.toRGB
    pixel(off + 0) = c.red.toByte
    pixel(off + 1) = c.green.toByte
    pixel(off + 2) = c.blue.toByte
  }
}

/** A ColorModel for grayscale colors. */
trait GreyColorModel extends ByteColorModel {
  val n_channel = 1
  def readARGB(pixel: Array[ChannelType], off: Int = 0): Int = (
    readChannel(pixel, off)(0) * 0x00010101 | 0xff000000
  )
  def writeARGB(pixel: Array[ChannelType], off: Int = 0)(argb: Int): PixelType = {
    writeColor(Color(argb))
  }

  def readColor(pixel: Array[ChannelType], off: Int = 0) = Color(readARGB(pixel))

  def writeColor(pixel: Array[ChannelType], off: Int = 0)(color: Color) = {
    val c = color.toRGB
    pixel(off) = (c.red * 0.2989f + c.green * 0.5870f + c.blue * 0.1140f).toByte
  }
}

/** A ColorModel that stores only one of the RGB channel. */
class RGBLayerColorModel(val channel: Int) extends ByteColorModel {
  val n_channel = 1
  def readARGB(pixel: Array[ChannelType], off: Int = 0): Int = (
    pixel(off) << (16 - channel * 8) | 0xff000000
  )
  def writeARGB(pixel: Array[ChannelType], off: Int = 0)(argb: Int): PixelType = {
    pixel(off) = (argb >> (16 - channel * 8)).toByte
  }

  def readColor(pixel: Array[ChannelType], off: Int = 0) = Color(readARGB(pixel))

  def writeColor(pixel: Array[ChannelType], off: Int = 0)(color: Color) =
    writeARGB(c.toRGB.argb)
}

