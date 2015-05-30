package com.sksamuel.scrimage.io

import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.nio.JpegWriter
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

/** @author Stephen Samuel */
class JpegWriterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/bird.jpg")).scaleTo(600, 400)

  test("jpeg compression happy path") {
    for ( i <- 0 to 100 by 10 ) {
      original.bytes(JpegWriter().withCompression(i)) // make sure no exceptions for each format level
    }
  }
}
