package com.sksamuel.scrimage.nio

import com.sksamuel.scrimage.Image
import org.apache.commons.io.IOUtils
import org.scalatest.{ Matchers, WordSpec }

/** @author Stephen Samuel */
class PngWriterTest extends WordSpec with Matchers {

  val original = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/bird.jpg")).scaleTo(300, 200)

  "png write" should {
    "png output happy path" ignore {
      val bytes = original.bytes(PngWriter())

      val expected = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/io/bird_300_200.png"))
      assert(expected.pixels.length === Image(bytes).pixels.length)
      assert(expected.pixels.deep == Image(bytes).pixels.deep)
      assert(expected == Image(bytes))
    }
    "png compression happy path" ignore {
      for (i <- 0 to 9) {

        val bytes = original.bytes(PngWriter().withCompression(i))

        val expected = Image(getClass.getResourceAsStream(s"/com/sksamuel/scrimage/io/bird_compressed_$i.png"))
        assert(expected.pixels.length === Image(bytes).pixels.length)
        assert(expected.pixels.deep == Image(bytes).pixels.deep)
        assert(expected == Image(bytes))
      }
    }
    "png reader detects the correct mime type" in {
      val bytes = IOUtils toByteArray getClass.getResourceAsStream("/com/sksamuel/scrimage/io/bird_300_200.png")
      PngReader.supports(bytes) shouldBe true
    }
    "png reader reads an image correctly" in {
      val expected = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/io/bird_300_200.png"))
      val actual = PngReader.read(IOUtils.toByteArray(getClass.getResourceAsStream("/com/sksamuel/scrimage/io/bird_300_200.png"))).get
      actual.output(new java.io.File("read_withPNGReader.png"))(PngWriter.MaxCompression)
      assert(actual.width === expected.width)
      assert(actual.height === expected.height)
      assert(actual === expected)
    }
  }
}