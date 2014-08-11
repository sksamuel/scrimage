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

trait Raster { self: ColorModel =>
  def width: Int
  def height: Int

  /** The type of this Raster */
  type RasterType <: Raster

  /** The underlying data representation */
  val model: Array[ChannelType]

  /** The number of channels used by this Raster */
  def n_channel: Int

  protected def offset(x: Int, y: Int): Int = (y * width + x) * n_channel

  /** The pixel at the given coordinates, as Int, in ARGB format */
  def pixel(x: Int, y: Int): Int = readARGB(model, offset(x, y))

  /** Returns the color of the pixel at the given x,y coordinate. */
  def readColor(x: Int, y: Int): Color = readColor(model, offset(x, y))

  /** Extracts the color of each pixels into an Array[Color] */
  def extract: Array[Color] = {
    val colors = Array.ofDim[Color](width * height)
    var x = 0
    while (x < width * height) {
      colors(x) = readColor(model, x * n_channel)
      x += 1
    }
  }

  /** Updates (mutates) this raster by setting the color of the pixel for the given x,y coordinate.
    *
    * @param x x-coordinate
    * @param y y-coordinate
    * @param color the pixel color
    */
  def write(x: Int, y: Int, color: Color) = writeColor(model, offset(x, y))(color)

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
  def empty(width: Int, height: Int): RasterType

  def fill(color: Color) = {
    var x = 0
    while (x < width * height) {
      writeColor(model, x * n_channel)(color)
      x += 1
    }
    this
  }

  /** Extracts a channel to an Array[ChannelType]. */
  def getChannel(channel: Int): Array[ChannelType] = {
    val out = Array.ofDim[ChannelType](width * height)
    var i = 0
    while (i < width * height) { out(i) = model(i * n_channel + channel); i += 1 }
    return out
  }

  /** Extracts all channels in an Array[Array[ChannelType]]. */
  def unpack(): Array[Array[ChannelType]] = {
    val unpacked: Array[Array[ChannelType]] = Array.fill(n_channel)(null)
    for (c <- 0 until n_channel) { unpacked(c) = getChannel(c) }
    unpacked
  }

  /** Sets a channel with the given values.
    * Used to fold channels succesively in an image, when channels are computed separately.
    */
  def foldChannel(channel: Int)(levels: Array[ChannelType]): Unit = {
    var i = 0
    while (i < width * height) { model(i * n_channel + channel) = levels(i); i += 1 }
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
  def copyWith(width: Int, height: Int, model: Array[Byte]): RasterType = {
    new ARGBRaster(width, height, model)
  }

  def empty(width: Int, height: Int): RasterType = {
    new ARGBRaster(width, height, Array.ofDim[Byte](width * height * n_channel))
  }

}

/** Factory for ARGBRaster */
object ARGBRaster {
  val void = new ARGBRaster(0, 0, Array.ofDim[Byte](0))
  def apply(width: Int, height: Int) = void.empty(width, height)
  def apply(width: Int, height: Int, color: Color) = {
    void.empty(width, height).fill(color)
  }
}
