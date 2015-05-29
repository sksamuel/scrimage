package com.sksamuel.scrimage.metadata

import org.scalatest.{Matchers, WordSpec}

/** @author Stephen Samuel */
class ImageMetadataTest extends WordSpec with Matchers {

  private val stream = getClass.getResourceAsStream("/vossen.jpg")

  "metadata" should {
    "read EXIF" in {
      val meta = ImageMetadata.fromStream(stream)
      meta.tags should contain(Tag("ISO Speed Ratings", 34855, "2500"))
      meta.tags should contain(Tag("Resolution Unit", 296, "Inch"))
      meta.tags should contain(Tag("White Balance Mode", 41987, "Auto white balance"))
    }
  }
}