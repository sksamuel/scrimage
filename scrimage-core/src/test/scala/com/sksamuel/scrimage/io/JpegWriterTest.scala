package com.sksamuel.scrimage.io

import org.scalatest.{ OneInstancePerTest, BeforeAndAfter, FunSuite }
import com.sksamuel.scrimage.{ Image, Format }

/** @author Stephen Samuel */
class JpegWriterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/bird.jpg")).scaleTo(600, 400)

  test("jpeg compression happy path") {
    for (i <- 0 to 100 by 10) {
      original.writer(Format.JPEG).withCompression(i).write() // make sure no exceptions for each format level
    }
  }
}
