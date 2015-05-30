package com.sksamuel.scrimage.nio

import java.io.InputStream

import com.sksamuel.scrimage.Image

/**
  * Typeclass supporting reading of an Image to an array of bytes in a specified foramt, eg JPEG
  */
trait ImageReader {
   def read(in: InputStream): Image
 }
