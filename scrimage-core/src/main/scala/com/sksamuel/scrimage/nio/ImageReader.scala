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

  private val readers = List(PngReader, JavaImageIOReader, JavaImageIO2Reader)

  def read(file: File): Image = read(file.toPath)

  def read(path: Path): Image = read(Files.newInputStream(path))

  def read(bytes: Array[Byte]): Image = {
    readers.foldLeft(None: Option[Image])((image, reader) =>
      image orElse {
        reader.read(bytes)
      }
    ).getOrElse(throw new ImageParseException)
  }

  def read(in: InputStream): Image = {
    val bytes = IOUtils.toByteArray(in)
    read(bytes)
  }

  def supports(in: InputStream): Boolean = Try {
    read(in)
  }.isSuccess
}

trait Reader {
  def read(bytes: Array[Byte]): Option[Image]
}