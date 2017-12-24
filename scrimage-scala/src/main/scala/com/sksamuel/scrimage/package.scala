package com.sksamuel

import com.sksamuel.scrimage.nio.{ImageWriter, PngWriter}

package object scrimage {
  implicit val writer: ImageWriter = PngWriter.MaxCompression
}
