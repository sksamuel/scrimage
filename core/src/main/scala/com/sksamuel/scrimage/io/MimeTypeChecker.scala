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
