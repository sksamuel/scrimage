package com.sksamuel.scrimage

/**
 *
 * A Raster is a data structure representing a rectangular grid of pixels.
 *
 *
 * @author Stephen Samuel
 **/
trait Raster {
  val width: Int
  val height: Int
  def copy: Raster
  def draw(i: Int, i1: Int, raster: Raster): Raster = null
  def read(x: Int, y: Int): Pixel
  def write(x: Int, y: Int, pixel: Pixel): Unit
  def subraster(x: Int, y: Int, w: Int, h: Int): Raster = null
  def map(function: Pixel => Pixel): Raster = null
  def setAll(pixel: Pixel): Raster = map(p => pixel)
}

object Raster {
  /**
   * Create a new default Raster of the specified size.
   */
  def apply(width: Int, height: Int): IntARGBRaster = new IntARGBRaster(width, height)
}

// raster that uses integers to represent all the information in a pixel
class IntARGBRaster(val width: Int, val height: Int) extends Raster {
  val pixels = Array(width * height)
  override def read(x: Int, y: Int): Pixel = null
  override def write(x: Int, y: Int, pixel: Pixel): Unit = ()
  override def copy: Raster = new IntARGBRaster(width, height)
}

// raster that uses a single byte per sample
class ByteARGBRaster(val width: Int, val height: Int) extends Raster {
  val pixels = Array(width * height * 4)
  override def read(x: Int, y: Int): Pixel = null
  override def write(x: Int, y: Int, pixel: Pixel): Unit = ()
  override def copy: Raster = new ByteARGBRaster(width, height)
}

