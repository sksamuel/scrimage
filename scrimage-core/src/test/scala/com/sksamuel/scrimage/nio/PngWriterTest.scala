package com.sksamuel.scrimage.nio

import com.sksamuel.scrimage.ImmutableImage
import org.apache.commons.io.IOUtils
import org.scalatest.{Matchers, WordSpec}

class PngWriterTest extends WordSpec with Matchers {

  implicit private val writer: PngWriter = PngWriter.MaxCompression
  private val original = ImmutableImage.fromResource("/com/sksamuel/scrimage/bird.jpg").scaleTo(300, 200)

  "png write" should {
    "png output happy path" in {
      val bytes = original.bytes
      val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/io/bird_300_200.png")
      assert(expected.pixels.length === ImmutableImage(bytes).pixels.length)
      assert(expected.pixels.toList == ImmutableImage(bytes).pixels.toList)
      assert(expected == ImmutableImage(bytes))
    }
    "png compression happy path" in {
      for ( i <- 0 to 9 ) {
        implicit val writer: PngWriter = PngWriter.NoCompression
        val bytes = original.bytes
        val expected = ImmutableImage.fromResource(s"/com/sksamuel/scrimage/io/bird_compressed_$i.png")
        assert(expected.pixels.length === ImmutableImage(bytes).pixels.length)
        assert(expected.pixels.toList == ImmutableImage(bytes).pixels.toList)
        assert(expected == ImmutableImage(bytes))
      }
    }
    "png reader detects the correct mime type" in {
      val bytes = IOUtils toByteArray getClass.getResourceAsStream("/com/sksamuel/scrimage/io/bird_300_200.png")
      new PngReader().supports(bytes) shouldBe true
    }
    "png reader reads an image correctly" in {
      val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/io/bird_300_200.png")
      val actual = new PngReader().fromBytes(IOUtils.toByteArray(getClass.getResourceAsStream("/com/sksamuel/scrimage/io/bird_300_200.png")))
      assert(actual.width === expected.width)
      assert(actual.height === expected.height)
      assert(actual === expected)
    }
    "png writer in scope by default" in {
      val bytes = original.bytes
    }
  }
}