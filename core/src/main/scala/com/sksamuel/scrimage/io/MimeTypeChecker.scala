package com.sksamuel.scrimage.io

/** Created by guw on 20/08/14.
  */
import java.io.InputStream

trait MimeType
case object PNGMimeType extends MimeType
case object GIFMimeType extends MimeType

trait MimeTypeChecker {
  def readMimeType(input: InputStream): Option[MimeType]
}

object PNGMimeTypeChecker extends MimeTypeChecker {

  def readMimeType(input: InputStream) = {
    try {
      val buff = Array.ofDim[Byte](8)
      input.read(buff)
      val expected = List(0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A) map (_.toByte)
      assert(buff.toList == expected)
      Some(PNGMimeType)
    } catch {
      case _: AssertionError => None
    }
  }
}
