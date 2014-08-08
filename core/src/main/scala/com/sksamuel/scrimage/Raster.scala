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

trait Raster {
  def width: Int
  def height: Int

  /** The type of this Raster */
  type RasterType <: Raster

  /** The type of the underlying data representation (usually Int or Byte) */
  type RawType
  val model: Array[RawType]

  /** The number of channels used by this Raster */
  def n_channel: Int

  /** The pixel at the given coordinates, as Int, in ARGB format */
  def pixel(x: Int, y: Int): Int

  /** Returns the color of the pixel at the given x,y coordinate. */
  def read(x: Int, y: Int): Color

  /** Extracts the color of each pixels into an Array[Color] */
  def extract: Array[Color]

  /** Updates (mutates) this raster by setting the color of the pixel for the given x,y coordinate.
    *
    * @param x x-coordinate
    * @param y y-coordinate
    * @param color the pixel color
    */
  def write(x: Int, y: Int, color: Color)

  /** Returns a new Raster which is a copy of this Raster.
    * Any changes made to the new Raster will not write back to this Raster.
    *
    * @return the copied Raster.
    */
  def copy: RasterType

  /** Returns a new Raster that is a subset of this Raster.
    *
    * @return a new Raster subset
    *
    */
  def patch(x: Int, y: Int, patchWidth: Int, patchHeight: Int): RasterType

  /** Returns a new Raster using the same color model but with the given width, height and data.
    */
  def copyWith(width: Int, height: Int, data: Array[RawType]): RasterType

  /** Returns an empty raster of the given size. */
  def empty(width: Int, height: Int): RasterType

  /** Extracts a channel to an Array[Byte]. */
  def getChannel(channel: Int): Array[Byte]

  /** Extracts all channels in an Array[Array[Byte]]. */
  def unpack(): Array[Array[Byte]]

  /** Sets a blank channel with the given values.
    * Used to fold channels succesively in a blank image, when channels are computed separately.
    */
  def foldChannel(channel: Int)(levels: Array[Byte]): Unit
}

/** A raster that uses a ColorModel to interpret it's data model.
  * The data model is an Array[PixelType] where PixelType is defined by the ColorModel
  */
trait ColoredRaster extends Raster { self: ColorModel =>

  type RawType = PixelType

  protected def coordinateToOffset(x: Int, y: Int): Int = y * width + x

  def pixel(x: Int, y: Int): Int = toARGB(model(coordinateToOffset(x, y)))

  def read(x: Int, y: Int): Color =
    toColor(model(coordinateToOffset(x, y)))

  def extract: Array[Color] = model.map(toColor)

  def write(x: Int, y: Int, color: Color): Unit =
    model(coordinateToOffset(x, y)) = fromColor(color)

  def copy: RasterType = copyWith(width, height, model.clone())

  def patch(x: Int, y: Int, patchWidth: Int, patchHeight: Int): RasterType = {
    val copy = newDataModel(patchWidth * patchHeight)
    for (i <- y until y + patchHeight) {
      System.arraycopy(model, coordinateToOffset(x, y), copy, y * patchWidth, patchWidth)
    }
    copyWith(patchWidth, patchHeight, copy)
  }

  def empty(width: Int, height: Int): RasterType =
    copyWith(width, height, newDataModel(width * height))

  def getChannel(channel: Int): Array[Byte] = model.map(c => self.getChannel(channel, c))

  def unpack(): Array[Array[Byte]] = {
    val unpacked: Array[Array[Byte]] = Array.fill(n_channel)(null)
    var c = 0
    while (c < n_channel) { unpacked(c) = Array.ofDim[Byte](width * height); c += 1 }

    var i = 0
    while (i < width * height) {
      val px = self.unpack(model(i))
      c = 0
      while (c < n_channel) { unpacked(c)(i) = px(c); c += 1 }
      i += 1
    }
    unpacked
  }

  def foldChannel(channel: Int)(levels: Array[Byte]): Unit = {
    var i = 0
    while (i < width * height) {
      model(i) = self.foldChannel(model(i), channel, levels(i))
      i += 1
    }
  }
}

/** Implementation of a Raster that packs ARGB information into a single integer.
  *
  * @param width number of columns in the raster
  * @param height number of rows in the raster
  */
class IntARGBRaster(val width: Int, val height: Int, val model: Array[Int])
    extends ColoredRaster with IntARGBColorModel {

  type RasterType = IntARGBRaster
  def copyWith(width: Int, height: Int, data: Array[RawType]): RasterType = {
    new IntARGBRaster(width, height, data)
  }
}

/** Factory for IntARGBRaster */
object IntARGBRaster {
  def apply(width: Int, height: Int) = new IntARGBRaster(width, height, new Array[Int](width * height))
  def apply(width: Int, height: Int, color: Color) = {
    val c = color.toRGB.argb
    new IntARGBRaster(width, height, Array.fill[Int](width * height)(c))
  }
}

/** A raster that uses an Array[Byte] and a [[BytesColorModel]] that also uses Array[Byte] as pixel type.
  */
trait BytesRaster extends Raster { self: BytesColorModel =>

  type RawType = Byte

  def pixel(x: Int, y: Int): Int = toARGB(model, y * width + x)
  def read(x: Int, y: Int): Color = toColor(model, y * width + x)

  def extract: Array[Color] = {
    val extracted = Array.ofDim[Color](width * height)
    for (x <- 0 until width; y <- 0 until height) {
      extracted(y * width + x) = read(x, y)
    }
    extracted
  }

  def write(x: Int, y: Int, color: Color): Unit =
    System.arraycopy(fromColor(color), 0, model, y * width + x, n_channel)

  def patch(x: Int, y: Int, patchWidth: Int, patchHeight: Int): RasterType = {
    val copy = Array.ofDim[Byte](patchWidth * patchHeight * n_channel)
    for (i <- y until y + patchHeight) {
      System.arraycopy(model, y * width * n_channel + x,
        copy, y * patchWidth * n_channel, patchWidth * n_channel)
    }
    copyWith(patchWidth, patchHeight, copy)
  }

  def copy: RasterType = copyWith(width, height, model.clone())

  def empty(width: Int, height: Int): RasterType =
    copyWith(width, height, Array.ofDim[Byte](width * height * n_channel))

  def getChannel(channel: Int): Array[Byte] = {
    val out = Array.ofDim[Byte](width * height)
    var i = 0
    while (i < width * height) { out(i) = model(i * n_channel + channel); i += 1 }
    return out
  }

  def unpack(): Array[Array[Byte]] = {
    val unpacked: Array[Array[Byte]] = Array.fill(n_channel)(null)
    for (c <- 0 until n_channel) { unpacked(c) = getChannel(c) }
    unpacked
  }

  def foldChannel(channel: Int)(levels: Array[Byte]): Unit = {
    var i = 0
    while (i < width * height) { model(i * n_channel + channel) = levels(i); i += 1 }
  }
}

class BytesARGBRaster(val width: Int, val height: Int, val model: Array[Byte])
    extends BytesRaster with BytesARGBColorModel {

  type RasterType = BytesARGBRaster

  def copyWith(width: Int, height: Int, data: Array[Byte]): RasterType = {
    new BytesARGBRaster(width, height, data)
  }
}

/** Factory for BytesARGBRaster */
object BytesARGBRaster {
  def apply(width: Int, height: Int): BytesARGBRaster =
    new BytesARGBRaster(width, height, Array.ofDim[Byte](width * height * 4))

  def apply(width: Int, height: Int, color: Color): BytesARGBRaster = {
    val raster = this(width, height)
    val c = color.toRGB
    val temp = Array.fill[Byte](width * height)(c.alpha.toByte)
    raster.foldChannel(0)(temp)
    java.util.Arrays.fill(temp, c.red.toByte)
    raster.foldChannel(1)(temp)
    java.util.Arrays.fill(temp, c.green.toByte)
    raster.foldChannel(2)(temp)
    java.util.Arrays.fill(temp, c.blue.toByte)
    raster.foldChannel(3)(temp)
    raster
  }
}

