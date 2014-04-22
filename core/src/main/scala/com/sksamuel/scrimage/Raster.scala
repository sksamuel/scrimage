package com.sksamuel.scrimage

/**
 *
 * A Raster is a data structure representing a rectangular grid of pixels.
 *
 *
 * @author Stephen Samuel
 **/
trait Raster {
  def read(x: Int, y: Int): Pixel
  def write(x: Int, y: Int, pixel: Pixel): Unit
}

// raster that uses integers to represent all the information in a pixel
class IntARGBRaster(width: Int, height: Int) extends Raster {
  private val pixels = Array(width * height)
  override def read(x: Int, y: Int): Pixel = null
  override def write(x: Int, y: Int, pixel: Pixel): Unit = ()
}
// raster that uses a single byte per sample
class ByteARGBRaster(width: Int, height: Int) extends Raster {
  private val pixels = Array(width * height * 4)
  override def read(x: Int, y: Int): Pixel = null
  override def write(x: Int, y: Int, pixel: Pixel): Unit = ()
}

