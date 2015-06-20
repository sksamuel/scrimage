package com.sksamuel.scrimage.nio

import java.io.OutputStream

import com.sksamuel.scrimage.AbstractImage

/**
 * Typeclass supporting writing of an Image to an array of bytes in a specified format, eg JPEG
 */
trait ImageWriter {
  def write(image: AbstractImage, out: OutputStream): Unit
}

