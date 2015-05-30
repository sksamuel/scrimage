package com.sksamuel.scrimage.nio

import java.io.InputStream

import com.sksamuel.scrimage.Image

import scala.util.Try

/**
 * Typeclass supporting reading of an Image to an array of bytes in a specified foramt, eg JPEG
 */
trait ImageReader {
  def read(in: InputStream): Image
  def supports(in: InputStream): Boolean = Try { read(in) }.isSuccess
}
