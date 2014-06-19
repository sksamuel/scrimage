package com.sksamuel.scrimage

/**
 * A Raster is a data structure representing a rectangular grid of pixels.
 * In Scrimage a Raster lies on the plane at 0,0 extending to width,height.
 *
 * For performance, Rasters are mutable data structures. Thus care should
 * be taken when sharing between multiple images.
 *
 * @author Stephen Samuel
 **/
trait Raster {

  def pixel(i: Int, i1: Int): Int = ???

  type PixelType
  val width: Int
  val height: Int
  val model: Array[PixelType]

  /**
   * Returns the color of the pixel at the given x,y coordinate.
   *
   * @param x x-coordinate
   * @param y y-coordinate
   *
   * @return the pixel color
   */
  def read(x: Int, y: Int): Color

  /**
   * Updates (mutates) this raster by setting the color of the pixel for the given x,y coordinate.
   *
   * @param x x-coordinate
   * @param y y-coordinate
   * @param color the pixel color
   */
  def write(x: Int, y: Int, color: Color): Unit

  protected def coordinateToOffset(x: Int, y: Int): Int = y * width + x

  /**
   * Returns a new Raster which is a copy of this Raster.
   * Any changes made to the new Raster will not write back to this Raster.
   *
   * @return the copied Raster.
   */
  def copy: Raster

  /**
   * Returns a new Raster that is a subset of this Raster.
   *
   * @return a new Raster subset
   *
   */
  def patch(col: Int, row: Int, width: Int, height: Int): Raster
}

/**
 * Implementation of a Raster that packs ARGB information into a single integer.
 *
 * @param width number of columns in the raster
 * @param height number of rows in the raster
 */
class IntARGBRaster(val width: Int,
                    val height: Int,
                    val model: Array[Int]) extends Raster {

  type PixelType = Int

  override def read(x: Int, y: Int): Color = model(coordinateToOffset(x, y))
  override def write(x: Int, y: Int, color: Color): Unit = {
    val offset = coordinateToOffset(x, y)
    model(offset) = color.toInt
  }

  def copy: IntARGBRaster = new IntARGBRaster(width, height, model.clone())

  override def patch(col: Int, row: Int, patchWidth: Int, patchHeight: Int): Raster = {
    val copy = IntARGBRaster(patchWidth, patchHeight)
    // todo optimize by using system array copy to copy row by row instead of this pixel by pixel
    for ( x1 <- 0 until patchWidth;
          y1 <- 0 until patchHeight ) {
      copy.write(x1 + col, y1 + row, read(x1, y1))
    }
    copy
  }
}

object IntARGBRaster {
  def apply(width: Int, height: Int) = new IntARGBRaster(width, height, new Array[Int](width * height))
  def apply(width: Int, height: Int, color: Color) = {
    new IntARGBRaster(width, height, Array(Nil.padTo(width * height, color.toInt): _*))
  }
}