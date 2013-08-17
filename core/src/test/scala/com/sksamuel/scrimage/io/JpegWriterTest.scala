package com.sksamuel.scrimage.io

import org.scalatest.{ OneInstancePerTest, BeforeAndAfter, FunSuite }
import com.sksamuel.scrimage.{ Image, Format }
import java.io.ByteArrayOutputStream

/** @author Stephen Samuel */
class JpegWriterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = Image(getClass.getResourceAsStream("/bird.jpg")).scaleTo(600, 400)

  test("jpeg compression happy path") {
    for (i <- 0 to 100 by 10) {

      val out = new ByteArrayOutputStream()
      original.writer(Format.JPEG).withCompression(i).write(out)

      val expected = Image(getClass.getResourceAsStream(s"/com/sksamuel/scrimage/io/bird_compressed_$i.jpg"))
      assert(expected.pixels.length === Image(out.toByteArray).pixels.length)
      assert(expected.pixels === Image(out.toByteArray).pixels)
      assert(expected === Image(out.toByteArray))
    }
  }
}
