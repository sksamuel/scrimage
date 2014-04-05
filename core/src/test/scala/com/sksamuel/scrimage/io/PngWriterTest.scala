package com.sksamuel.scrimage.io

import org.scalatest.{ OneInstancePerTest, BeforeAndAfter, FunSuite }
import com.sksamuel.scrimage.{ Format, Image }
import java.io.ByteArrayOutputStream

/** @author Stephen Samuel */
class PngWriterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/bird.jpg")).scaleTo(300, 200)

  test("png output happy path") {
    val out = new ByteArrayOutputStream()
    original.write(out, Format.PNG)

    val expected = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/io/bird_300_200.png"))
    assert(expected.pixels.length === Image(out.toByteArray).pixels.length)
    assert(expected.pixels.deep == Image(out.toByteArray).pixels.deep)
    assert(expected == Image(out.toByteArray))
  }

  test("png compression happy path") {
    for (i <- 0 to 9) {

      val out = new ByteArrayOutputStream()
      original.writer(Format.PNG).withCompression(i).write(out)

      val expected = Image(getClass.getResourceAsStream(s"/com/sksamuel/scrimage/io/bird_compressed_$i.png"))
      assert(expected.pixels.length === Image(out.toByteArray).pixels.length)
      assert(expected.pixels.deep == Image(out.toByteArray).pixels.deep)
      assert(expected == Image(out.toByteArray))
    }
  }
}
