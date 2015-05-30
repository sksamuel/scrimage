package com.sksamuel.scrimage

import java.io.InputStream

import com.sksamuel.scrimage.nio.{ GifReader, JpegReader, PngReader }

class FormatDetector {
  def detect(in: InputStream): Option[Format] = {
    if (PngReader.supports(in)) Some apply Format.PNG
    else if (JpegReader.supports(in)) Some apply Format.PNG
    else if (GifReader.supports(in)) Some apply Format.PNG
    else None
  }
}

sealed trait Format
object Format {
  case object PNG extends Format
  case object GIF extends Format
  case object JPEG extends Format
}