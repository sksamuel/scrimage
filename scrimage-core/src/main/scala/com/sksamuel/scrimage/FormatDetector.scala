package com.sksamuel.scrimage

import java.io.InputStream

class FormatDetector {
  // inspired by http://stackoverflow.com/questions/22534833/scala-detect-mimetype-of-an-arraybyte-image
  def detect(in: InputStream): Option[Format] = {
    val bytes = List.fill(8)(in.read.toByte)
    if (bytes.take(4) == List('G', 'I', 'F', '8')) Some(Format.GIF)
    else if (bytes.take(8) == List[Char](0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A)) Some(Format.PNG)
    else if (bytes.take(4) == List[Char](0xFF, 0xD8, 0xFF, 0xEE)) Some(Format.JPEG)
    else if (bytes.take(3) == List[Char](0xFF, 0xD8, 0xFF)) Some(Format.JPEG)
    else None
  }
}

sealed trait Format
object Format {
  case object PNG extends Format
  case object GIF extends Format
  case object JPEG extends Format
}