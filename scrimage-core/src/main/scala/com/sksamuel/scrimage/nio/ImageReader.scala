package com.sksamuel.scrimage.nio

import java.io.{File, InputStream}
import java.nio.file.{Files, Path}

import com.sksamuel.scrimage.{Image, ImageParseException}
import org.apache.commons.io.IOUtils

import scala.util.Try

/**
 * Utilites for reading of an Image to an array of bytes in a specified format.
 */
object ImageReader {

  private val readers = List(JavaImageIOReader, PngReader, JavaImageIO2Reader)

  def fromFile(file: File, `type`: Int = Image.CANONICAL_DATA_TYPE): Image = {
    fromPath(file.toPath, `type`)
  }

  def fromPath(path: Path, `type`: Int = Image.CANONICAL_DATA_TYPE): Image = {
    fromStream(Files.newInputStream(path), `type`)
  }

  def fromStream(in: InputStream, `type`: Int = Image.CANONICAL_DATA_TYPE): Image = {
    val bytes = IOUtils.toByteArray(in)
    fromBytes(bytes, `type`)
  }

  def fromBytes(bytes: Array[Byte], `type`: Int = Image.CANONICAL_DATA_TYPE): Image = {
    readers.foldLeft(None: Option[Image])((image, reader) =>
      image orElse {
        Try {
          reader.fromBytes(bytes, `type`)
        } getOrElse None
      }
    ).getOrElse(throw new ImageParseException)
  }

}

trait Reader {
  def fromBytes(bytes: Array[Byte], `type`: Int = Image.CANONICAL_DATA_TYPE): Option[Image]
}