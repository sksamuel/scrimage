package com.sksamuel.scrimage.io

import org.scalatest.{ OneInstancePerTest, BeforeAndAfter, FunSuite }

/** @author Stephen Samuel */
class TiffReaderTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val in = getClass.getResourceAsStream("/com/sksamuel/scrimage/io/example.tiff")

  test("tiff file reads pixels") {
    val actual = TiffReader.read(in)
    assert(500 === actual.width)
    assert(300 === actual.height)
    assert(actual.argb(120, 83) === Array(255, 215, 81, 86))
    assert(actual.argb(273, 280) === Array(255, 0, 102, 89))
  }
}
