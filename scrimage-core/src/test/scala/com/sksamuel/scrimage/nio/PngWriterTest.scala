package com.sksamuel.scrimage.nio

import java.io.File

import com.sksamuel.scrimage.Image
import org.apache.commons.io.IOUtils
import org.scalatest.{Matchers, WordSpec}

/** @author Stephen Samuel */
class PngWriterTest extends WordSpec with Matchers {

  val original = Image.fromResource("/com/sksamuel/scrimage/bird.jpg").scaleTo(300, 200)

  "png write" should {
    "png output happy path" in {
      val bytes = original.bytes
      val expected = Image.fromResource("/com/sksamuel/scrimage/io/bird_300_200.png")
      assert(expected.pixels.length === Image(bytes).pixels.length)
      assert(expected.pixels.deep == Image(bytes).pixels.deep)
      assert(expected == Image(bytes))
    }
    "png compression happy path" in {
      for ( i <- 0 to 9 ) {
        val bytes = original.bytes(PngWriter().withCompression(i))
        val expected = Image.fromResource(s"/com/sksamuel/scrimage/io/bird_compressed_$i.png")
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
      val expected = Image.fromResource("/com/sksamuel/scrimage/io/bird_300_200.png")
      val actual = PngReader
        .fromBytes(IOUtils.toByteArray(getClass.getResourceAsStream("/com/sksamuel/scrimage/io/bird_300_200.png")))
        .get
      assert(actual.width === expected.width)
      assert(actual.height === expected.height)
      assert(actual === expected)
    }
    "preserve metadata" ignore {
      val image = Image.fromResource("/com/sksamuel/scrimage/metadata/treelandscape.png")
      println(image.metadata.tags.map(_.name))
      val file = image.output(File.createTempFile("metadata", "png"))
      val image2 = Image.fromFile(file)
      println(image2.metadata.tags.map(_.name))
      for ( tag <- image.metadata.tags ) {
        image2.metadata.tags.find(_.name == tag.name) shouldBe Some(tag)
      }
    }
  }
}