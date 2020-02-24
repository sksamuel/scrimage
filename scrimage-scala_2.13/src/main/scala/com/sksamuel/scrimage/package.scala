package com.sksamuel

import com.sksamuel.scrimage.color.Color
import com.sksamuel.scrimage.nio.PngWriter

import scala.language.implicitConversions

package object scrimage {
   implicit val writer: ImageWriter = PngWriter.MaxCompression
   implicit def color2awt(color: Color): java.awt.Color = color.awt();
}
