package com.sksamuel

import com.sksamuel.scrimage.nio.{PngWriter, ImageWriter}

package object scrimage {
  implicit val writer: ImageWriter = PngWriter.MaxCompression
}
