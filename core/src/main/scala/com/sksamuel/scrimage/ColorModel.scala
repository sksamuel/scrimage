package com.sksamuel.scrimage

/** Describes how to read Color from raw data.
  * The ColorModel may use several channels to encode the color,
  * and must provide a way to access to each of this channel.
  *
  * @author Guillaume Wenzek
  */
trait ColorModel {

  /** The data type that contains pixel information in this color model.
    * For example, if you were implementing an ARGB color model, you may chose to use ints
    * and pack the 4 components into an int using bit shifting, or you may choose to use 4 bytes.
    */
  @specialized(Byte)
  type ChannelType

  /** The number of channels used by this ColorModel */
  val n_channel: Int

  /** The maximum value that should be written in a channel*/
  val maxChannelValue: Int

  /** Reads one channel in the given array at the given offset. */
  def readChannel(offset: Int, pixel: Array[ChannelType]): Int

  /** Writes one channel in the given array at the given offset.
    * May have unexpected result if level is bigger than maxChannelValue.
    */
  def writeChannel(offset: Int, pixel: Array[ChannelType])(level: Int): Unit

  /** Reads a color at the given offset and returns it in the ARGB format. */
  def readARGB(offset: Int, pixel: Array[ChannelType]): Int

  /** Writes a color, given in the ARGB format, at the given offset. */
  def writeARGB(offset: Int, pixel: Array[ChannelType])(argb: Int): Unit

  /** Reads a color at the given offset */
  def readColor(offset: Int, pixel: Array[ChannelType]): Color

  /** Writes a color at the given offset */
  def writeColor(offset: Int, pixel: Array[ChannelType])(color: Color): Unit

  /** Creates an array of ChannelType of the correct size for the given dim of raster */
  def newDataModel(width: Int, height: Int): Array[ChannelType]
}

/** Stores channels value ranges between 0 and 255 and are stored in separated bytes */
trait ByteColorModel extends ColorModel {

  final type ChannelType = Byte
  final val maxChannelValue = 255

  @inline
  final def readChannel(off: Int, pixel: Array[ChannelType]) = pixel(off) & 0xff

  @inline
  final def writeChannel(off: Int, pixel: Array[ChannelType])(level: Int) =
    pixel(off) = level.toByte

  final def newDataModel(width: Int, height: Int) =
    Array.ofDim[Byte](width * height * n_channel)
}

/** Stores 4 component in 4 bytes in the order: Red, Green, Blue, Alpha (cf PNG standards). */
trait RGBAColorModel extends ByteColorModel {
  final val n_channel = 4
  final def readARGB(off: Int, pixel: Array[ChannelType]) = (
    readChannel(off + 3, pixel) << 24 |
    readChannel(off + 0, pixel) << 16 |
    readChannel(off + 1, pixel) << 8 |
    readChannel(off + 2, pixel))

  final def writeARGB(off: Int, pixel: Array[ChannelType])(argb: Int) = {
    pixel(off + 0) = (argb >> 16).toByte
    pixel(off + 1) = (argb >> 8).toByte
    pixel(off + 2) = argb.toByte
    pixel(off + 3) = (argb >> 24).toByte
  }
  final def readColor(off: Int, pixel: Array[ChannelType]) =
    Color(
      readChannel(off + 0, pixel),
      readChannel(off + 1, pixel),
      readChannel(off + 2, pixel),
      readChannel(off + 3, pixel))

  final def writeColor(off: Int, pixel: Array[ChannelType])(color: Color) = {
    val c = color.toRGB
    pixel(off + 0) = c.red.toByte
    pixel(off + 1) = c.green.toByte
    pixel(off + 2) = c.blue.toByte
    pixel(off + 3) = c.alpha.toByte
  }
}

/** Stores 3 component in 3 bytes in the order: Red, Green, Blue (cf PNG standards). */
trait RGBColorModel extends ByteColorModel {
  final val n_channel = 3
  final def readARGB(off: Int, pixel: Array[ChannelType]) = (
    0xff000000 |
    readChannel(off + 0, pixel) << 16 |
    readChannel(off + 1, pixel) << 8 |
    readChannel(off + 2, pixel))

  final def writeARGB(off: Int, pixel: Array[ChannelType])(argb: Int) = {
    pixel(off + 0) = (argb >> 16).toByte
    pixel(off + 1) = (argb >> 8).toByte
    pixel(off + 2) = argb.toByte
  }
  final def readColor(off: Int, pixel: Array[ChannelType]) =
    Color(
      readChannel(off + 0, pixel),
      readChannel(off + 1, pixel),
      readChannel(off + 2, pixel))

  def writeColor(off: Int, pixel: Array[ChannelType])(color: Color) = {
    val c = color.toRGB
    pixel(off + 0) = c.red.toByte
    pixel(off + 1) = c.green.toByte
    pixel(off + 2) = c.blue.toByte
  }
}

/** Stores the grayscale level in one byte. */
trait GrayColorModel extends ByteColorModel {
  final val n_channel = 1
  final def readARGB(off: Int, pixel: Array[ChannelType]) = readChannel(off, pixel) * 0x00010101 | 0xff000000
  final def writeARGB(off: Int, pixel: Array[ChannelType])(argb: Int) = {
    writeColor(off, pixel)(Color(argb))
  }
  final def readColor(off: Int, pixel: Array[ChannelType]) = Color(readARGB(off, pixel))
  final def writeColor(off: Int, pixel: Array[ChannelType])(color: Color) = {
    val c = color.toRGB
    pixel(off) = (c.red * 0.2989f + c.green * 0.5870f + c.blue * 0.1140f).toByte
  }
}

/** Stores the grayscale and alpha levels in two bytes (cf PNG standards). */
trait GrayAlphaColorModel extends ByteColorModel {
  final val n_channel = 2
  final def readARGB(off: Int, pixel: Array[ChannelType]) =
    readChannel(off, pixel) * 0x00010101 | readChannel(off, pixel) << 24
  final def writeARGB(off: Int, pixel: Array[ChannelType])(argb: Int) = {
    writeColor(off, pixel)(Color(argb))
  }
  final def readColor(off: Int, pixel: Array[ChannelType]) = Color(readARGB(off, pixel))
  final def writeColor(off: Int, pixel: Array[ChannelType])(color: Color) = {
    val c = color.toRGB
    pixel(off) = (c.red * 0.2989f + c.green * 0.5870f + c.blue * 0.1140f).toByte
    pixel(off + 1) = c.alpha.toByte
  }
}

/** Stores only one of the RGB channel. */
class RGBLayerColorModel(val channel: Int) extends ByteColorModel {
  final val n_channel = 1
  final def readARGB(off: Int, pixel: Array[ChannelType]) = (
    pixel(off) << (16 - channel * 8) | 0xff000000
  )
  final def writeARGB(off: Int, pixel: Array[ChannelType])(argb: Int) = {
    pixel(off) = (argb >> (16 - channel * 8)).toByte
  }

  final def readColor(off: Int, pixel: Array[ChannelType]) = Color(readARGB(off, pixel))

  final def writeColor(off: Int, pixel: Array[ChannelType])(color: Color) =
    writeARGB(off, pixel)(color.toRGB.argb)
}

