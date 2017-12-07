package com.sksamuel.scrimage.nio

import java.io.{IOException, OutputStream}

import com.sksamuel.scrimage.Image

/**
  * Typeclass supporting writing of an Image to an array of bytes in a specified format, eg JPEG
  */
trait ImageWriter {
  @throws[IOException]
  def write(image: Image, out: OutputStream): Unit
}

object ImageWriter {
  implicit def default: PngWriter = PngWriter.MaxCompression
}