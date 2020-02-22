package com.sksamuel.scrimage.nio

import com.sksamuel.scrimage.ImmutableImage
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class JpegWriterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  private val original = ImmutableImage.fromResource("/com/sksamuel/scrimage/bird.jpg").scaleTo(600, 400)

  test("jpeg compression happy path") {
    for (i <- 0 to 100 by 10) {
      original.bytes(new JpegWriter().withCompression(i)) // make sure no exceptions for each format level
    }
  }

  test("issue 84 - jpeg writing with alpha") {
    val img = ImmutableImage.fromResource("/com/sksamuel/scrimage/nio/issue84.jpg")
    val w = new JpegWriter()
    img.bytes(w) // was throwing with bug
  }
}
