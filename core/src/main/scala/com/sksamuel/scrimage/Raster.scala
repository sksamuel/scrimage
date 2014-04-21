package com.sksamuel.scrimage

/**
 *
 * A Raster is a data structure representing a rectangular grid of pixels.
 *
 *
 * @author Stephen Samuel
 **/
trait Raster {
  def flipX: Raster = ???

  def flipY: Raster = ???

  val width: Int
  val height: Int
  def copy: Raster
  def draw(i: Int, i1: Int, raster: Raster): Raster = null
  def pixel(i: Int, i1: Int): Int = ???
  def read(x: Int, y: Int): ARGBPixel
  def write(x: Int, y: Int, pixel: ARGBPixel): Unit
  def subraster(x: Int, y: Int, w: Int, h: Int): Raster = null
  def map(function: ARGBPixel => ARGBPixel): Raster = null
  def setAll(pixel: ARGBPixel): Raster = map(p => pixel)
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
  override def read(x: Int, y: Int): ARGBPixel = null
  override def write(x: Int, y: Int, pixel: ARGBPixel): Unit = ()
  override def copy: Raster = new IntARGBRaster(width, height)
}

// raster that uses a single byte per sample
class ByteARGBRaster(val width: Int, val height: Int) extends Raster {
  val pixels = Array(width * height * 4)
  override def read(x: Int, y: Int): ARGBPixel = null
  override def write(x: Int, y: Int, pixel: ARGBPixel): Unit = ()
  override def copy: Raster = new ByteARGBRaster(width, height)
}

