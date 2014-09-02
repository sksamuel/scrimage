package com.sksamuel.scrimage.io

import java.io.ByteArrayOutputStream

import com.sksamuel.scrimage.{Format, Image}
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

/** @author Stephen Samuel */
class PngWriterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/bird.jpg")).scaleTo(300, 200)

  ignore("png output happy path") {
    val out = new ByteArrayOutputStream()
    original.write(out, Format.PNG)

    val expected = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/io/bird_300_200.png"))
    assert(expected.pixels.length === Image(out.toByteArray).pixels.length)
    assert(expected.pixels.deep == Image(out.toByteArray).pixels.deep)
    assert(expected == Image(out.toByteArray))
  }

  ignore("png compression happy path") {
    for (i <- 0 to 9) {

      val out = new ByteArrayOutputStream()
      original.writer(Format.PNG).withCompression(i).write(out)

      val expected = Image(getClass.getResourceAsStream(s"/com/sksamuel/scrimage/io/bird_compressed_$i.png"))
      assert(expected.pixels.length === Image(out.toByteArray).pixels.length)
      assert(expected.pixels.deep == Image(out.toByteArray).pixels.deep)
      assert(expected == Image(out.toByteArray))
    }
  }

  test("png reader detects the correct mime type") {
    val mime = PNGReader.readMimeType(getClass.getResourceAsStream("/com/sksamuel/scrimage/io/bird_300_200.png"))
    assert(mime === Some(Format.PNG))
  }

  test("png reader reads an image correctly") {
    val expected = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/io/bird_300_200.png"))
    val read = PNGReader.read(getClass.getResourceAsStream("/com/sksamuel/scrimage/io/bird_300_200.png"))
    read.write(new java.io.File("read_withPNGReader.png"))
    assert(read.width === expected.width)
    assert(read.height === expected.height)
    assert(read === expected)
  }
}
