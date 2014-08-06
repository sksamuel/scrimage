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
trait Raster extends { self: ColorModel =>

  val width: Int
  val height: Int

  /** The representation of the pixels */
  val model: Array[PixelType]
  val n_channel: Int

  /** The type of this Raster */
  type RasterType <: Raster

  /** The pixel at the given coordinates, as Int, in ARGB format */
  def pixel(x: Int, y: Int): Int = toARGB(model(coordinateToOffset(x, y)))

  /** Returns the color of the pixel at the given x,y coordinate. */
  def read(x: Int, y: Int): Color =
    toColor(model(coordinateToOffset(x, y)))

  /** Extracts the color of each pixels into an Array[Color] */
  def extract: Array[Color] = model.map(toColor)

  /** Updates (mutates) this raster by setting the color of the pixel for the given x,y coordinate.
    *
    * @param x x-coordinate
    * @param y y-coordinate
    * @param color the pixel color
    */
  def write(x: Int, y: Int, color: Color): Unit =
    model(coordinateToOffset(x, y)) = fromColor(color)

  protected def coordinateToOffset(x: Int, y: Int): Int = y * width + x

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
  def patch(col: Int, row: Int, patchWidth: Int, patchHeight: Int): RasterType = {
    val copy = newDataModel(patchWidth * patchHeight)
    for (y1 <- 0 until patchHeight) {
      System.arraycopy(model, coordinateToOffset(0, y1 + row), copy, y1 * patchWidth, patchWidth)
    }
    copyWith(patchWidth, patchHeight, copy)
  }

  /** Returns a new Raster using the same color model but with the given width, height and data.
    */
  def copyWith(width: Int, height: Int, data: Array[PixelType]): RasterType

  /** Returns an empty raster of the given size. */
  def empty(width: Int, height: Int): RasterType =
    copyWith(width, height, newDataModel(width * height))

  /** Extracts a channel to an Array[Byte]. */
  def getChannel(channel: Int): Array[Byte] = model.map(c => self.getChannel(channel, c))

  /** Extracts all channels in an Array[Array[Byte]]. */
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

  /** Sets one of the channel with the given values */
  def foldChannel(channel: Int)(levels: Array[Byte]): Unit = {
    var i = 0
    while (i < width * height) {
      model(i) = self.foldChannel(model(i), channel, levels(i))
      i += 1
    }
  }

  /** Copies a patch of the data into the given Array[Pixel] */
  def getDataElements(x: Int, y: Int, w: Int, h: Int, out: Array[PixelType]): Array[PixelType] = {
    for (i <- y until y + h) {
      System.arraycopy(model, coordinateToOffset(x, y), out, y * w, w)
    }
    out
  }
}

/** Implementation of a Raster that packs ARGB information into a single integer.
  *
  * @param width number of columns in the raster
  * @param height number of rows in the raster
  */
class IntARGBRaster(val width: Int, val height: Int, val model: Array[Int])
    extends Raster with IntARGBColorModel {

  type RasterType = IntARGBRaster

  def copyWith(width: Int, height: Int, data: Array[Int]): RasterType = {
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

