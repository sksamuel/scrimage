package com.sksamuel.scrimage

import com.sksamuel.scrimage.color.Color
import org.scalatest.{Matchers, WordSpec}

class Issue144Test extends WordSpec with Matchers {

  "Cover resize" should {
    "not produce a black bar on an image with an odd size" in {
      val img = ImmutableImage.filled(2357, 2400, Color.White)

      val resized = img.cover(200, 200)
      val pixel   = resized.pixel(199, 0)

      pixel.red shouldBe 255
      pixel.blue shouldBe 255
      pixel.green shouldBe 255
    }
  }

}
