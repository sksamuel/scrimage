package com.sksamuel.scrimage

import java.io.{ OutputStream, File, ByteArrayOutputStream }
import java.nio.file.{ Files, Paths, Path }

import com.sksamuel.scrimage.nio.ImageWriter

class WriteContext(writer: ImageWriter, image: Image) {

  def bytes: Array[Byte] = {
    val bos = new ByteArrayOutputStream
    writer.write(image, bos)
    bos.toByteArray
  }

  def write(path: String): Path = write(Paths.get(path))

  def write(file: File): File = {
    write(file.toPath)
    file
  }

  def write(path: Path): Path = {
    val out = Files.newOutputStream(path)
    writer.write(image, out)
    out.close()
    path
  }

  def write(out: OutputStream): Unit = writer.write(image, out)
}
