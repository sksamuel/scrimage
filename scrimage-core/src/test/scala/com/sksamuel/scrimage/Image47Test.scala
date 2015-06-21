package com.sksamuel.scrimage

import org.scalatest.{Matchers, WordSpec}

class Image47Test extends WordSpec with Matchers {

  "loading iphone image" should {
    "detect rotation flag" in {
      val src = Image.fromResource("/issue47.JPG")
      src.output("iphone_rotated.png")
      src.height shouldBe 3264
      src.width shouldBe 2448
    }
  }
}
