package com.sksamuel.scrimage

/** A Raster is a data structure representing a rectangular grid of pixels.
  * In Scrimage a Raster lies on the plane at 0,0 extending to width,height.
  *
  * For performance, Rasters are mutable data structures. Thus care should
  * be taken when sharing between multiple images.
  *
  * @author Stephen Samuel
  */
trait Raster extends { self: ColorModel =>

  val width: Int
  val height: Int

  override type PixelType
  val model: Array[PixelType]

  type RasterType <: Raster

  def pixel(x: Int, y: Int): Int = toARGB(model(coordinateToOffset(x, y)))

  /** Returns the color of the pixel at the given x,y coordinate.
    *
    * @param x x-coordinate
    * @param y y-coordinate
    *
    * @return the pixel color
    */
  def read(x: Int, y: Int): Color =
    toColor(model(coordinateToOffset(x, y)))

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
  def patch(col: Int, row: Int, patchWidth: Int, patchHeight: Int): RasterType

  /** Returns a new Raster using the same color model but with the given width, height and data
    * @type {[type]}
    */
  def copyWith(width: Int, height: Int, data: Array[PixelType]): RasterType
  def empty(width: Int, height: Int): RasterType

  def getComp(n: Int): Array[Byte] = model.map(c => self.getComp(n, c))

  val n_comp: Int
  def unpack(): Array[Array[Byte]] = {
    val unpacked: Array[Array[Byte]] = Array.fill(n_comp)(null)
    var comp = 0
    while (comp < n_comp) { unpacked(comp) = Array.ofDim[Byte](width * height); comp += 1 }

    var i = 0
    while (i < width * height) {
      val px = self.unpack(model(i))
      comp = 0
      while (comp < n_comp) {
        unpacked(comp)(i) = px(comp)
        comp += 1
      }
      i += 1
    }
    unpacked
  }

  def foldComp(comp: Int)(pixels: Array[Byte]): Unit = {
    var i = 0
    while (i < width * height) {
      model(i) = self.foldComp(model(i), comp, pixels(i))
      i += 1
    }
  }

  def extract: Array[Color] = model.map(toColor)

  def getDataElements(x: Int, y: Int, w: Int, h: Int, out: Array[PixelType]): Array[PixelType] = {
    for (i <- y until y + h) {
      System.arraycopy(model, coordinateToOffset(x, y), out, y * w, w)
    }
    return out
  }
}

/** Implementation of a Raster that packs ARGB information into a single integer.
  *
  * @param width number of columns in the raster
  * @param height number of rows in the raster
  */
class IntARGBRaster(val width: Int, val height: Int, val model: Array[Int])
    extends Raster with ARGBColorModel {

  type RasterType = IntARGBRaster

  def copyWith(width: Int, height: Int, data: Array[Int]): RasterType = {
    new IntARGBRaster(width, height, data)
  }

  def patch(col: Int, row: Int, patchWidth: Int, patchHeight: Int): RasterType = {
    val copy = newDataModel(patchWidth * patchHeight)
    for (y1 <- 0 until patchHeight) {
      System.arraycopy(model, coordinateToOffset(0, y1 + row), copy, y1 * patchWidth, patchWidth)
    }
    copyWith(patchWidth, patchHeight, copy)
  }

  def empty(width: Int, height: Int): RasterType = {
    new IntARGBRaster(width, height, Array.ofDim[Int](width * height))
  }
}

object IntARGBRaster {
  def apply(width: Int, height: Int) = new IntARGBRaster(width, height, new Array[Int](width * height))
  def apply(width: Int, height: Int, color: Color) = {
    val c = color.toRGB.argb
    new IntARGBRaster(width, height, Array.fill[Int](width * height)(c))
  }
}

