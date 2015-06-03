package com.sksamuel.scrimage

/**
 * Operations that can be implemented using only an array of pixels
 */
trait PixelOps[R] {

  /**
   * @return the width of the image
   */
  def width: Int

  /**
   * @return the height of the image
   */
  def height: Int

  /**
   * Returns all the pixels for the image
   *
   * @return an array of pixels for this image
   */
  def pixels: Array[Pixel] = iterator.toArray

  /**
   * Returns the pixel at the given coordinates.
   *
   * @param x the x coordinate of the pixel to grab
   * @param y the y coordinate of the pixel to grab
   *
   * @return the Pixel at the location
   */
  def pixel(x: Int, y: Int): Pixel

  /**
   * Returns the pixels of the image as an iterator.
   * The iterator is the most efficient way to lazily iterator over the pixels as the pixels will only
   * be fetched from the raster as needed.
   * @return the iterator
   */
  def iterator: Iterator[Pixel]

  /**
   * Returns the pixel at the given coordinate.
   * @param p the pixel as an integer tuple
   * @return the pixel
   */
  def pixel(p: (Int, Int)): Pixel = pixel(p._1, p._2)

  // This tuple contains all the state that identifies this particular image.
  private[scrimage] def imageState = (width, height, pixels.toList)

  // See this Stack Overflow question to see why this is implemented this way.
  // http://stackoverflow.com/questions/7370925/what-is-the-standard-idiom-for-implementing-equals-and-hashcode-in-scala
  override def hashCode: Int = imageState.hashCode

  override def equals(other: Any): Boolean = {
    other match {
      case that: PixelOps[_] =>
        this.width == that.width &&
          this.height == that.height &&
          (iterator sameElements that.iterator)
      case _ => false
    }
  }

  lazy val points: Seq[(Int, Int)] = for (x <- 0 until width; y <- 0 until height) yield (x, y)

  /**
   * Returns the number of pixels in the image.
   * @return the number of pixels
   */
  lazy val count: Int = width * height

  /**
   * Returns the centre coordinates for the image.
   */
  lazy val center: (Int, Int) = (width / 2, height / 2)
  lazy val radius: Int = Math.sqrt(Math.pow(width / 2.0, 2) + Math.pow(height / 2.0, 2)).toInt
  lazy val dimensions: (Int, Int) = (width, height)

  /**
   * @return Returns the aspect ratio for this image.
   */
  lazy val ratio: Double = if (height == 0) 0 else width / height.toDouble

  def forall(f: (Int, Int, Pixel) => Boolean): Boolean = points.forall(p => f(p._1, p._2, pixel(p)))
  def foreach(f: (Int, Int, Pixel) => Unit): Unit = points.foreach(p => f(p._1, p._2, pixel(p)))

  def row(y: Int): Array[Pixel] = pixels(0, y, width, 1)
  def col(x: Int): Array[Pixel] = pixels(x, 0, 1, height)

  /**
   * Returns true if a pixel with the given color exists.
   *
   * @param color the pixel colour to look for.
   * @return true if there exists at least one pixel that has the given pixels color
   */
  def contains(color: Color): Boolean = exists(p => p.toInt == color.toPixel.toInt)

  /**
   * Returns true if the predicate holds on the image
   * @param p a predicate
   * @return true if p holds for at least one pixel
   */
  def exists(p: Pixel => Boolean): Boolean = iterator.exists(p)

  /**
   * Returns the color at the given coordinates.
   *
   * @return the RGBColor value at the coords
   */
  def color(x: Int, y: Int): RGBColor = pixel(x, y).toInt

  /**
   * Returns the ARGB components for the pixel at the given coordinates
   *
   * @param x the x coordinate of the pixel component to grab
   * @param y the y coordinate of the pixel component to grab
   *
   * @return an array containing ARGB components in that order.
   */
  def argb(x: Int, y: Int): Array[Int] = {
    val p = pixel(x, y)
    Array(p.alpha, p.red, p.green, p.blue)
  }

  /**
   * Returns the ARGB components for all pixels in this image
   *
   * @return an array containing ARGB components in that order.
   */
  def argb: Array[Array[Int]] = {
    pixels.map(p => Array(p.alpha, p.red, p.green, p.blue))
  }

  def rgb(x: Int, y: Int): Array[Int] = {
    val p = pixel(x, y)
    Array(p.red, p.green, p.blue)
  }

  def rgb: Array[Array[Int]] = {
    pixels.map(p => Array(p.red, p.green, p.blue))
  }

  /**
   * Returns a rectangular region within the given boundaries as a single
   * dimensional array of integers.
   *
   * Eg, pixels(10, 10, 30, 20) would result in an array of size 600 with
   * the first row of the region in indexes 0,..,29, second row 30,..,59 etc.
   *
   * @param x the start x coordinate
   * @param y the start y coordinate
   * @param w the width of the region
   * @param h the height of the region
   * @return an Array of pixels for the region
   */
  def pixels(x: Int, y: Int, w: Int, h: Int): Array[Pixel] = {
    for (
      y1 <- Array.range(y, y + h);
      x1 <- Array.range(x, x + w)
    ) yield pixel(x1, y1)
  }

  /**
   * Returns a set of the distinct colours used in this image.
   * @return the set of distinct Colors
   */
  def colours: Set[RGBColor] = iterator.map(pixel => pixel.toColor).toSet

  /**
   * Counts the number of pixels with the given colour.
   *
   * @param color the colour to detect.
   * @return the number of pixels that matched the colour of the given pixel
   */
  def count(color: Color): Int = iterator.count(_.toColor == color)

  /**
   * Counts the number of pixels that are true for the given predicate
   * @param p a predicate
   * @return the number of pixels that evaluated true
   */
  def count(p: Pixel => Boolean): Int = iterator.count(p)

  /**
   * Creates a new image with the same data as this image.
   * Any operations to the copied image will not write back to the original.
   *
   * @return A copy of this image.
   */
  def copy: Image

  /**
   * Creates an empty Image with the same dimensions of this image.
   *
   * @return a new Image that is a clone of this image but with uninitialized data
   */
  def blank: Image
}
