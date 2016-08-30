package com.sksamuel.scrimage

import org.scalatest.{Matchers, WordSpec}

class Issue47Test extends WordSpec with Matchers {

  "loading iphone image" should {
    "detect rotation flag" in {
      val src = Image.fromResource("/issue47.JPG")
      src.height shouldBe 3264
      src.width shouldBe 2448
    }
  }
}
