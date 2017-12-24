package com.sksamuel.scrimage.scala.nio

import com.sksamuel.scrimage.nio.JpegWriter

object JpegWriter {
  val NoCompression = new JpegWriter(100, false)
  val Default = new JpegWriter(80, false)
  def apply(): JpegWriter = Default
}
