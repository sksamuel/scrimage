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

  val width: Int
  val height: Int

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
   * Returns a new Raster whr
   * @return
   */
  def copy: Raster
}

/**
 * Implementation of a Raster that packs ARGB information into a single integer.
 *
 * @param width number of columns in the raster
 * @param height number of rows in the raster
 */
class IntARGBRaster(val width: Int,
                    val height: Int,
                    model: Array[Int] = new Array[Int](width * height)) extends Raster {
  override def read(x: Int, y: Int): Color = model(coordinateToOffset(x, y))
  override def write(x: Int, y: Int, color: Color): Unit = {
    val offset = coordinateToOffset(x, y)
    model(offset) = color.toInt
  }
  def copy: IntARGBRaster = new IntARGBRaster(width, height, model.clone())
}