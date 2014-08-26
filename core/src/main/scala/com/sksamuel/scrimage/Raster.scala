package com.sksamuel.scrimage

/** A Raster is a data structure representing a rectangular grid of pixels.
  * In Scrimage a Raster lies on the plane at 0,0 extending to width,height.
  *
  * For performance, Rasters are mutable data structures. Thus care should
  * be taken when sharing between multiple images.
  *
  * A Raster needs a ColorModel to be instantiated and to make sense of it's data model.
  *
  * Rasters are created trough the compagnion object Raster.
  *
  * @author Stephen Samuel
  */

trait Raster { self: ColorModel =>

  /** Number of columns in the raster */
  val width: Int

  /** Number of rows in the raster */
  val height: Int

  /** The type of this Raster */
  type RasterType <: Raster

  /** The underlying data representation */
  val model: Array[ChannelType]

  /** The number of channels used by this Raster (herited from the ColorModel)  */
  val n_channel: Int

  /** The max value you should write in this Raster (herited from the ColorModel) */
  val maxChannelValue: Int

  /** Returns the adresse where to read the pixel (x, y) in the model. */
  @inline
  def offset(x: Int, y: Int): Int = n_channel * (y * width + x)

  /** Returns the adresse where to read the channel c of pixel (x, y) in the model. */
  @inline
  def offset(x: Int, y: Int, c: Int): Int = n_channel * (y * width + x) + c

  /** The pixel at the given coordinates, as Int, in ARGB format */
  def pixel(x: Int, y: Int): Int = readARGB(offset(x, y), model)

  /** Returns the color of the pixel at the given x,y coordinate. */
  def read(x: Int, y: Int): Color = readColor(offset(x, y), model)

  /** Updates (mutates) this raster by setting the color of the pixel for the given x,y coordinate.
    */
  def write(x: Int, y: Int, color: Color) = writeColor(offset(x, y), model)(color)

  /** Returns the level of the channel c of the pixel (x, y). */
  def readChannel(x: Int, y: Int, c: Int): Int = readChannel(offset(x, y, c), model)

  /** Returns the level at the given offset.
    * Used for fast access successives channels  (herited from the ColorModel).
    */
  @inline def readChannel(off: Int, model: Array[ChannelType] = model): Int

  /** Sets the level of the channel c of the pixel (x, y). */
  def writeChannel(x: Int, y: Int, c: Int)(level: Int): Unit = writeChannel(offset(x, y, c), model)(level)

  /** Set the level at the given offset.
    * Used for fast access successives channels (herited from the ColorModel).
    */
  @inline def writeChannel(off: Int, model: Array[ChannelType] = model)(level: Int): Unit

  /** Extracts the color of each pixels into an Array[Color] */
  def extract = {
    val colors = Array.ofDim[Color](width * height)
    var x = 0
    var off = 0
    while (x < width * height) {
      colors(x) = readColor(off, model)
      x += 1
      off += n_channel
    }
    colors
  }

  /** Returns an Iterator over the colors contained in this Raster.
    * Colors are returned row by row, left to right and top to bottom.
    */
  def read: Iterable[Color] = {
    for (x <- 0 until width * height) yield readColor(x * n_channel, model)
  }

  /** Set all the pixels with the given colors.
    * Pixels are set row by row, left to right and top to bottom.
    */
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

  /** Fills the raster with given color. */
  def fill(color: Color) = {
    var x = 0
    while (x < width * height) {
      writeColor(x * n_channel, model)(color)
      x += 1
    }
    this
  }

}

/** Uses the RGBAColorModel */
class ARGBRaster(val width: Int, val height: Int, val model: Array[Byte])
    extends Raster with RGBAColorModel {

  type RasterType = ARGBRaster
  def copyWith(width: Int, height: Int, model: Array[ChannelType]): RasterType = {
    new ARGBRaster(width, height, model)
  }
}

/** Uses the RGBColorModel */
class RGBRaster(val width: Int, val height: Int, val model: Array[Byte])
    extends Raster with RGBColorModel {
  type RasterType = RGBRaster
  def copyWith(width: Int, height: Int, model: Array[ChannelType]): RasterType = {
    new RGBRaster(width, height, model)
  }
}

/** Uses the GrayColorModel */
class GrayRaster(val width: Int, val height: Int, val model: Array[Byte])
    extends Raster with GrayColorModel {
  type RasterType = GrayRaster
  def copyWith(width: Int, height: Int, model: Array[ChannelType]): RasterType = {
    new GrayRaster(width, height, model)
  }
}

/** Uses the GrayAlphaColorModel */
class GrayAlphaRaster(val width: Int, val height: Int, val model: Array[Byte])
    extends Raster with GrayAlphaColorModel {
  type RasterType = GrayAlphaRaster
  def copyWith(width: Int, height: Int, model: Array[ChannelType]): RasterType = {
    new GrayAlphaRaster(width, height, model)
  }
}

/** Factory for Raster */
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
}
