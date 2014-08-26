package com.sksamuel.scrimage

/** A Raster is a data structure representing a rectangular grid of pixels.
  * In Scrimage a Raster lies on the plane at 0,0 extending to width,height.
  *
  * For performance, Rasters are mutable data structures. Thus care should
  * be taken when sharing between multiple images.
  *
  * A Raster embeds a ColorModel to make sense of it's
  *
  * @author Stephen Samuel
  */

trait Raster { self: ByteColorModel =>
  val width: Int
  val height: Int

  /** The type of this Raster */
  type RasterType <: Raster

  /** The underlying data representation */
  val model: Array[ChannelType]

  /** The number of channels used by this Raster */
  val n_channel: Int
  val maxChannelValue: Int

  def offset(x: Int, y: Int): Int =
    n_channel * channelSize * (y * width + x)

  def offset(x: Int, y: Int, c: Int): Int =
    (n_channel * (y * width + x) + c) * channelSize

  /** The pixel at the given coordinates, as Int, in ARGB format */
  def pixel(x: Int, y: Int): Int = readARGB(offset(x, y), model)

  /** Returns the color of the pixel at the given x,y coordinate. */
  def read(x: Int, y: Int): Color = readColor(offset(x, y), model)

  /** Updates (mutates) this raster by setting the color of the pixel for the given x,y coordinate.
    */
  def write(x: Int, y: Int, color: Color) = writeColor(offset(x, y), model)(color)

  def readChannel(x: Int, y: Int, c: Int): Int =
    readChannel(offset(x, y, c), model)

  @inline
  def readChannel(off: Int, model: Array[ChannelType] = model): Int

  def writeChannel(x: Int, y: Int, c: Int)(level: Int): Unit =
    writeChannel(offset(x, y, c), model)(level)

  @inline
  def writeChannel(off: Int, model: Array[ChannelType] = model)(level: Int): Unit

  def writeChannels(x: Int, y: Int)(levels: Array[Int]): Unit = {
    var c = offset(x, y)
    var i = 0
    while (i < levels.length) {
      writeChannel(c, model)(levels(i))
      c += channelSize
      i += 1
    }
  }

  /** Extracts the color of each pixels into an Array[Color] */
  def extract = {
    val colors = Array.ofDim[Color](width * height)
    var x = 0
    var off = 0
    val sampleSize = n_channel * channelSize
    while (x < width * height) {
      colors(x) = readColor(off, model)
      x += 1
      off += sampleSize
    }
    colors
  }

  def read: Iterable[Color] = {
    for (x <- 0 until width * height) yield readColor(x * n_channel, model)
  }

  def write(colors: Iterable[Color]) = {
    val it = colors.toIterator
    var x = 0
    val xmax = width * height
    while (x < xmax && it.hasNext) {
      writeColor(x * n_channel, model)(it.next())
      x += 1
    }
    this
  }

  /** Returns a new Raster which is a copy of this Raster.
    * Any changes made to the new Raster will not write back to this Raster.
    *
    * @return the copied Raster.
    */
  def copy: RasterType = copyWith(width, height, model.clone())

  /** Returns a new Raster that is a subset of this Raster.
    *
    * @return a new Raster subset
    *
    */
  def patch(x: Int, y: Int, patchWidth: Int, patchHeight: Int): RasterType = {
    val copy = newDataModel(patchWidth, patchHeight)
    for (i <- y until y + patchHeight) {
      System.arraycopy(model, offset(x, y), copy, offset(0, y), patchWidth * n_channel)
    }
    copyWith(patchWidth, patchHeight, copy)
  }

  /** Returns a new Raster using the same color model but with the given width, height and data.
    */
  def copyWith(width: Int, height: Int, data: Array[ChannelType]): RasterType

  /** Returns an empty raster of the given size. */
  def empty(width: Int, height: Int): RasterType = {
    copyWith(width, height, newDataModel(width, height))
  }

  def fill(color: Color) = {
    var x = 0
    while (x < width * height) {
      writeColor(x * n_channel, model)(color)
      x += 1
    }
    this
  }

}

/** Implementation of a Raster that saves ARGB informations in Bytes.
  *
  * @param width number of columns in the raster
  * @param height number of rows in the raster
  */
class ARGBRaster(val width: Int, val height: Int, val model: Array[Byte])
    extends Raster with ARGBColorModel {

  type RasterType = ARGBRaster
  def copyWith(width: Int, height: Int, model: Array[ChannelType]): RasterType = {
    new ARGBRaster(width, height, model)
  }
}

/** Implementation of a Raster that saves RGB informations in Bytes.
  *
  * @param width number of columns in the raster
  * @param height number of rows in the raster
  */
class RGBRaster(val width: Int, val height: Int, val model: Array[Byte])
    extends Raster with RGBColorModel {
  type RasterType = RGBRaster
  def copyWith(width: Int, height: Int, model: Array[ChannelType]): RasterType = {
    new RGBRaster(width, height, model)
  }
}

class GrayRaster(val width: Int, val height: Int, val model: Array[Byte])
    extends Raster with GrayColorModel {
  type RasterType = GrayRaster
  def copyWith(width: Int, height: Int, model: Array[ChannelType]): RasterType = {
    new GrayRaster(width, height, model)
  }
}

class GrayAlphaRaster(val width: Int, val height: Int, val model: Array[Byte])
    extends Raster with GrayAlphaColorModel {
  type RasterType = GrayAlphaRaster
  def copyWith(width: Int, height: Int, model: Array[ChannelType]): RasterType = {
    new GrayAlphaRaster(width, height, model)
  }
}

object Raster {
  trait RasterType
  object GRAY extends RasterType
  object GRAY_ALPHA extends RasterType
  object RGB extends RasterType
  object ARGB extends RasterType

  def apply(width: Int, height: Int): Raster = apply(width, height, ARGB)

  def apply(width: Int, height: Int, rasterType: RasterType): Raster = rasterType match {
    case ARGB => new ARGBRaster(width, height, Array.ofDim[Byte](width * height * 4))
    case RGB => new RGBRaster(width, height, Array.ofDim[Byte](width * height * 3))
    case GRAY => new GrayRaster(width, height, Array.ofDim[Byte](width * height * 1))
    case GRAY_ALPHA => new GrayAlphaRaster(width, height, Array.ofDim[Byte](width * height * 2))
  }

  def apply(width: Int, height: Int, model: Array[Byte], rasterType: RasterType): Raster = rasterType match {
    case ARGB => new ARGBRaster(width, height, model)
    case RGB => new RGBRaster(width, height, model)
    case GRAY => new GrayRaster(width, height, model)
    case GRAY_ALPHA => new GrayAlphaRaster(width, height, model)
  }

  def apply(width: Int, height: Int, colors: Iterable[Color], rasterType: RasterType): Raster =
    Raster(width, height, rasterType) write colors

  def withARGB(width: Int, height: Int, argbColors: Iterable[Int], rasterType: RasterType): Raster =
    Raster(width, height, rasterType) write (argbColors map (argb => Color(argb)))

  def getType(channels: Int, bitDepth: Int): RasterType = {
    if (bitDepth <= 8) {
      if (channels == 4) ARGB
      else if (channels == 3) RGB
      else if (channels == 2) GRAY_ALPHA
      else if (channels == 1) GRAY
      else throw new RuntimeException(s"Too much channels: $channels")
    } else throw new RuntimeException(s"Bit depth not supported: $bitDepth")
  }
}

/** Factory for ARGBRaster */
object ARGBRaster {
  def apply(width: Int, height: Int) = Raster(width, height, Raster.ARGB)
  def apply(width: Int, height: Int, color: Color) = {
    Raster(width, height, Raster.ARGB).fill(color)
  }

  def apply(width: Int, height: Int, colors: Array[Int]) = {
    Raster(width, height, Raster.ARGB).write(colors.map(argb => Color(argb)))
  }

  def apply(width: Int, height: Int, colors: Iterable[Color]) = Raster(width, height, colors, Raster.ARGB)

  def apply(width: Int, height: Int, channels: Array[Byte]) = {
    new ARGBRaster(width, height, channels)
  }
}
