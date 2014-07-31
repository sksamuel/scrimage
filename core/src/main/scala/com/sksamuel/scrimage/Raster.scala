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

  val colorModel : ColorModel
  // type PixelType = colorModel.PixelType
  val width: Int
  val height: Int
  val model: Array[colorModel.PixelType]

  def pixel(x: Int, y: Int): Int = ???


  /**
   * Returns the color of the pixel at the given x,y coordinate.
   *
   * @param x x-coordinate
   * @param y y-coordinate
   *
   * @return the pixel color
   */
  def read(x: Int, y: Int): Color =
    colorModel.toColor(model(coordinateToOffset(x, y)))

  /**
   * Updates (mutates) this raster by setting the color of the pixel for the given x,y coordinate.
   *
   * @param x x-coordinate
   * @param y y-coordinate
   * @param color the pixel color
   */
  def write(x: Int, y: Int, color: Color): Unit =
    model(coordinateToOffset(x, y)) = colorModel.fromColor(color)


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

  def getComp(n: Int): Array[Int] = model.map(colorModel.getComp(n))

  def extract: Array[Color] = model.map(colorModel.toColor)
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

  val colorModel = ARGBColorModel

  override def pixel(x: Int, y: Int): Int = model(coordinateToOffset(x, y))

  def copy: IntARGBRaster = new IntARGBRaster(width, height, model.clone())

  override def patch(col: Int, row: Int, patchWidth: Int, patchHeight: Int): Raster = {
    // todo optimize by using system array copy to copy row by row instead of this pixel by pixel
    // for ( x1 <- 0 until patchWidth;
    //       y1 <- 0 until patchHeight ) {
    //   copy.write(x1 + col, y1 + row, read(x1, y1))
    // }
    val copy = IntARGBRaster(patchWidth, patchHeight)
    for(y1 <- 0 until patchHeight){
      System.arraycopy(model, coordinateToOffset(0, y1), copy, y1*patchWidth, patchWidth)
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

