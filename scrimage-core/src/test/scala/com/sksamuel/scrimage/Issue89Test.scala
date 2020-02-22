package com.sksamuel.scrimage

import com.sksamuel.scrimage.nio.JpegWriter
import org.scalatest.{Matchers, WordSpec}

class Issue89Test extends WordSpec with Matchers {

  "Loading image" should {
    "not randomly rotate" in {
      for ( k <- 1 to 10 ) {
        val image = ImmutableImage.fromResource("/issue89.jpg")
        image.width shouldBe 319
        image.height shouldBe 397
        val bytes = image.bytes(new JpegWriter())
        val reread = ImmutableImage(bytes)
        reread.width shouldBe 319
        reread.height shouldBe 397
      }
    }
  }
}
