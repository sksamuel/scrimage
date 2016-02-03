package com.sksamuel.scrimage

import java.io.{ByteArrayInputStream, OutputStream, File, ByteArrayOutputStream}
import java.nio.file.{Files, Paths, Path}

import com.sksamuel.scrimage.nio.ImageWriter

class WriteContext(writer: ImageWriter, image: Image) extends Using {

  def bytes: Array[Byte] = {
    val bos = new ByteArrayOutputStream
    writer.write(image, bos)
    bos.toByteArray
  }

  def stream: ByteArrayInputStream = new ByteArrayInputStream(bytes)

  def write(path: String): Path = write(Paths.get(path))

  def write(file: File): File = {
    write(file.toPath)
    file
  }

  def write(path: Path): Path = {
    using(Files.newOutputStream(path)) { out =>
      writer.write(image, out)
    }
    path
  }

  def write(out: OutputStream): Unit = writer.write(image, out)
}
