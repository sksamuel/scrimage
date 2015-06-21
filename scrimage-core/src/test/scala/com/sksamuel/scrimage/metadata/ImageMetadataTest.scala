package com.sksamuel.scrimage.metadata

import com.sksamuel.scrimage.{Tag, ImageMetadata}
import org.scalatest.{ Matchers, WordSpec }

/** @author Stephen Samuel */
class ImageMetadataTest extends WordSpec with Matchers {

  private val stream = getClass.getResourceAsStream("/vossen.jpg")

  "metadata" should {
    "read EXIF" in {
      val meta = ImageMetadata.fromStream(stream)
      meta.tags should contain(Tag("ISO Speed Ratings", 34855, "2500", "2500"))
      meta.tags should contain(Tag("Image Width", 256, "4928", "4928 pixels"))
      meta.tags should contain(Tag("White Balance Mode", 41987, "0", "Auto white balance"))
    }
  }
}