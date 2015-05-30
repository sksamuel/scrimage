package com.sksamuel.scrimage.nio

import java.io.{ ByteArrayInputStream, File, InputStream }
import java.nio.file.{ Files, Path }

import com.sksamuel.scrimage.Image

import scala.util.Try

/**
 * Typeclass supporting reading of an Image to an array of bytes in a specified foramt, eg JPEG
 */
trait ImageReader {
  def read(file: File): Image = read(file.toPath)
  def read(path: Path): Image = read(Files.newInputStream(path))
  def read(bytes: Array[Byte]): Image = read(new ByteArrayInputStream(bytes))
  def read(in: InputStream): Image
  def supports(in: InputStream): Boolean = Try {
    read(in)
  }.isSuccess
}
