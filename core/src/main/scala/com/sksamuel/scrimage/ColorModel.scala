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
  type PixelType

  /** The number of channels used by this ColorModel
    */
  val n_channel: Int

  /** Extracts the channel value of a given pixel.
    * The result is a signed Byte (use & 0xff to convert to an Int between 0 and 255).
    *
    * @param channel - the channel asked
    * @return The channel value as a Byte
    */
  def getChannel(channel: Int, pixel: PixelType): Byte

  /** Extracts all the channels of the given pixels into an Array[Byte].
    *
    * @param pixel - the pixel to unpack
    * @param out - an optionnal array to store the result. If none given, one will be created.
    * @return Return value - blah blah
    */
  def unpack(pixel: PixelType, out: Array[Byte] = Array.ofDim[Byte](n_channel)): Array[Byte]

  /** Packs the given array of byte into a Pixel.
    *
    * @param channels - the array to pack into a pixel
    * @return Return value - blah blah
    */
  def pack(channels: Array[Byte]): PixelType

  /** Converts a pixel to an ARGB Int. */
  def toARGB(pixel: PixelType): Int

  /** Converts an ARGB Int to a Pixel. */
  def fromARGB(color: Int): PixelType

  /** Converts a pixel to a [[com.sksamuel.scrimage.Color]]. */
  def toColor(pixel: PixelType): Color

  /** Converts a [[com.sksamuel.scrimage.Color]] to a Pixel. */
  def fromColor(color: Color): PixelType

  /** Sets a channel of a pixel with the given Byte. Assumes that this channel was 0 before
    *
    * @param pixel - the pixel to modify
    * @param channel - the channel to set
    * @param level - the level of this channel
    * @return the result pixel
    */
  def foldChannel(pixel: PixelType, channel: Int, level: Byte): PixelType

  /** Creates an array of Pixel of the given size. */
  def newDataModel(size: Int): Array[PixelType]
}

/** A ColorModel that packs a pixel into an Int. */
trait IntColorModel extends ColorModel {
  type PixelType = Int
  def newDataModel(size: Int) = Array.ofDim[Int](size)
}

/** A ColorModel that packs ARGB pixels in an Int. */
trait IntARGBColorModel extends IntColorModel {
  val n_channel = 4
  def getChannel(channel: Int, pixel: PixelType): Byte = (pixel >> (24 - channel * 8)).toByte
  def pack(channels: Array[Byte]): PixelType =
    (channels(0) & 0xff) << 24 | (channels(1) & 0xff) << 16 | (channels(2) & 0xff) << 8 | (channels(3) & 0xff)
  def unpack(pixel: PixelType, out: Array[Byte] = Array.ofDim[Byte](n_channel)): Array[Byte] = {
    out(0) = (pixel >> 24).toByte
    out(1) = (pixel >> 16).toByte
    out(2) = (pixel >> 8).toByte
    out(3) = pixel.toByte
    out
  }
  def toARGB(px: PixelType): Int = px
  def fromARGB(c: Int): PixelType = c
  def toColor(pixel: PixelType) = Color(toARGB(pixel))
  def fromColor(c: Color) = fromARGB(c.toRGB.argb)
  def foldChannel(px: PixelType, channel: Int, c: Byte) = px | (c & 0xff) << (24 - channel * 8)
}

/** A ColorModel that packs RGB pixels in an Int. */
trait RGBColorModel extends IntColorModel {
  val n_channel = 3
  def getChannel(channel: Int, pixel: PixelType): Byte = (pixel >> (16 - channel * 8)).toByte
  def pack(channels: Array[Byte]): PixelType =
    (channels(0) & 0xff) << 16 | (channels(1) & 0xff) << 8 | (channels(2) & 0xff)
  def unpack(pixel: PixelType, out: Array[Byte] = Array.ofDim[Byte](n_channel)): Array[Byte] = {
    out(0) = (pixel >> 16).toByte
    out(1) = (pixel >> 8).toByte
    out(2) = pixel.toByte
    out
  }
  def toARGB(px: PixelType): Int = px | 0xff000000
  def fromARGB(c: Int): PixelType = c
  def toColor(pixel: PixelType) = Color(toARGB(pixel))
  def fromColor(c: Color) = fromARGB(c.toRGB.argb)
  def foldChannel(px: PixelType, channel: Int, c: Byte) = px | (c & 0xff) << (16 - channel * 8)
}

/** A ColorModel with one channel of one Byte. */
trait ByteColorModel extends ColorModel {
  type PixelType = Byte
  val n_channel = 1
  def getChannel(channel: Int, pixel: PixelType): Byte = pixel
  def pack(channels: Array[Byte]): PixelType = channels(0)
  def unpack(pixel: PixelType, out: Array[Byte] = Array.ofDim[Byte](n_channel)): Array[Byte] = {
    out(0) = pixel
    out
  }
  def foldChannel(pixel: PixelType, channel: Int, c: Byte): PixelType = c
  def newDataModel(size: Int) = Array.ofDim[PixelType](size)
}

/** A ColorModel for grayscale colors. */
trait GreyColorModel extends ByteColorModel {
  def toARGB(pixel: PixelType): Int = (pixel & 0xff) * 0x00010101 | 0xff000000
  def fromARGB(c: Int): PixelType = fromColor(Color(c))
  def toColor(pixel: PixelType) = Color(toARGB(pixel))
  def fromColor(c: Color) = {
    val rgb = c.toRGB
    (rgb.red * 0.2989f + rgb.green * 0.5870f + rgb.blue * 0.1140f).toByte
  }
}

/** A ColorModel that stores only one of the RGB channel. */
class RGBLayerColorModel(val channel: Int) extends ByteColorModel {
  def toARGB(pixel: PixelType): Int = pixel << (16 - channel * 8) | 0xff000000
  def fromARGB(c: Int): PixelType = (c >> (16 - channel * 8)).toByte
  def toColor(pixel: PixelType) = Color(toARGB(pixel))
  def fromColor(c: Color) = fromARGB(c.toRGB.argb)
}

/** A ColorModel that packs a pixel into several bytes. */
trait BytesColorModel extends ColorModel {
  type PixelType = Array[Byte]
  def toARGB(pixel: PixelType) = toARGB(pixel, 0)
  def toARGB(pixel: PixelType, off: Int): Int
  def toColor(pixel: PixelType) = toColor(pixel, 0)
  def toColor(pixel: PixelType, off: Int): Color
  def fromARGB(argb: Int) = fromARGB(argb, Array.ofDim[Byte](n_channel), 0)
  def fromARGB(argb: Int, out: Array[Byte], offset: Int): PixelType
  def fromColor(c: Color) = fromARGB(c.toRGB.argb)
  def newDataModel(size: Int) = Array.ofDim[Byte](size, n_channel)
  def getChannel(channel: Int, pixel: PixelType): Byte = pixel(channel)
  def pack(channels: Array[Byte]): PixelType = channels
  def unpack(pixel: PixelType, out: Array[Byte] = Array.ofDim[Byte](n_channel)): Array[Byte] = {
    System.arraycopy(pixel, 0, out, 0, n_channel)
    out
  }
  def foldChannel(px: PixelType, channel: Int, c: Byte) = {
    px(channel) = c; px
  }
}

trait BytesARGBColorModel extends BytesColorModel {
  val n_channel = 4
  def toARGB(pixel: PixelType, off: Int): Int = (
    (pixel(off + 0) & 0xff) << 24 |
    (pixel(off + 1) & 0xff) << 16 |
    (pixel(off + 2) & 0xff) << 8 |
    (pixel(off + 3) & 0xff)
  )
  def fromARGB(argb: Int, out: Array[Byte], offset: Int): PixelType = {
    out(offset) = (argb >> 24).toByte
    out(offset + 1) = (argb >> 16).toByte
    out(offset + 2) = (argb >> 8).toByte
    out(offset + 3) = argb.toByte
    out
  }
  def toColor(pixel: PixelType, off: Int) =
    Color(pixel(off + 1), pixel(off + 2), pixel(off + 3), pixel(off + 0))

}

class BytesRGBColorModel extends BytesColorModel {
  val n_channel = 3
  def toARGB(pixel: PixelType, off: Int): Int =
    (pixel(off + 0) & 0xff) << 16 | (pixel(off + 1) & 0xff) << 8 | (pixel(off + 2) & 0xff)

  def fromARGB(argb: Int, out: Array[Byte], offset: Int): PixelType = {
    out(offset) = (argb >> 16).toByte
    out(offset + 1) = (argb >> 8).toByte
    out(offset + 2) = argb.toByte
    out
  }
  def toColor(pixel: PixelType, off: Int) = Color(pixel(off + 0), pixel(off + 1), pixel(off + 2))
}
