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
  val channelSize: Int
  val max_channel_value: Int

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
  def extract: Array[Color] = {
    val colors = Array.ofDim[Color](width * height)
    var x = 0
    while (x < width * height) {
      colors(x) = readColor(x * n_channel, model)
      x += 1
    }
    colors
  }

  def write(colors: Array[_ <: Color]) = {
    var x = 0
    while (x < colors.length) {
      writeColor(x * n_channel, model)(colors(x))
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
  val GRAY = 0
  val GRAY_ALPHA = 4
  val RGB = 2
  val ARGB = 6
  def apply(width: Int, height: Int): Raster = apply(width, height, ARGB)

  def apply(width: Int, height: Int, colorModel: Int): Raster = {
    if (colorModel == ARGB) new ARGBRaster(width, height, Array.ofDim[Byte](width * height * 4))
    else if (colorModel == RGB) new RGBRaster(width, height, Array.ofDim[Byte](width * height * 3))
    else if (colorModel == GRAY) new GrayRaster(width, height, Array.ofDim[Byte](width * height * 1))
    else if (colorModel == GRAY_ALPHA) new GrayAlphaRaster(width, height, Array.ofDim[Byte](width * height * 2))
    else throw new RuntimeException("Unknown colorModel. Use the PNG convention.")
  }

  def apply(width: Int, height: Int, model: Array[Byte], colorModel: Int): Raster = {
    if (colorModel == ARGB) new ARGBRaster(width, height, model)
    else if (colorModel == RGB) new RGBRaster(width, height, model)
    else if (colorModel == GRAY) new GrayRaster(width, height, model)
    else if (colorModel == GRAY_ALPHA) new GrayAlphaRaster(width, height, model)
    else throw new RuntimeException("Unknown colorModel. Use the PNG convention.")
  }

  def apply(width: Int, height: Int, colors: Array[_ <: Color], colorModel: Int): Raster =
    Raster(width, height, colorModel).write(colors)

  def apply(width: Int, height: Int, argbColors: Array[Int], colorModel: Int): Raster =
    Raster(width, height, colorModel).write(argbColors map (argb => Color(argb)))
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
  def apply(width: Int, height: Int, colors: Array[_ <: Color]) = Raster(width, height, colors, Raster.ARGB)

  def apply(width: Int, height: Int, channels: Array[Byte]) = {
    new ARGBRaster(width, height, channels)
  }
}
