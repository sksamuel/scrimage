package com.sksamuel.scrimage

import java.io.InputStream

object FormatDetector {

  // inspired by http://stackoverflow.com/questions/22534833/scala-detect-mimetype-of-an-arraybyte-image
  private lazy val gif: Array[Byte] = Array('G', 'I', 'F', '8').map(_.toByte)
  private lazy val png: Array[Byte] = Array[Char](0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A).map(_.toByte)
  private lazy val jpeg1: Array[Byte] = Array[Char](0xFF, 0xD8, 0xFF, 0xEE).map(_.toByte)
  private lazy val jpeg2: Array[Byte] = Array[Char](0xFF, 0xD8, 0xFF).map(_.toByte)

  def detect(in: InputStream): Option[Format] = {
    val bytes: Array[Byte] = Array.fill(8)(in.read.toByte)
    detect(bytes)
  }

  def detect(bytes: Array[Byte]): Option[Format] = {
    if (bytes.take(4) sameElements gif) Some(Format.GIF)
    else if (bytes.take(8) sameElements png) Some(Format.PNG)
    else if (bytes.take(4) sameElements jpeg1) Some(Format.JPEG)
    else if (bytes.take(3) sameElements jpeg2) Some(Format.JPEG)
    else None
  }
}

sealed trait Format
object Format {
  case object PNG extends Format
  case object GIF extends Format
  case object JPEG extends Format
}