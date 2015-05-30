package com.sksamuel.scrimage.io

import java.io.{InputStream, OutputStream}

import com.sksamuel.scrimage.Image

/**
 * Typeclass supporting writing of an Image to an array of bytes in a specified format, eg JPEG
 */
trait Writer {
  def write(image: Image, out: OutputStream): Unit
}

/**
 * Typeclass supporting reading of an Image to an array of bytes in a specified foramt, eg JPEG
 */
trait Reader {
  def write(in: InputStream): Image
}