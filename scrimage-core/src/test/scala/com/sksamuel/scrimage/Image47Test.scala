package com.sksamuel.scrimage

import org.scalatest.{Matchers, WordSpec}

class Image47Test extends WordSpec with Matchers {

  "cover" should {
    "not rotate image from iphone" in {
      val src = Image.fromResource("/issue47.JPG")
      src.scale(0.25).output("test.png")
    }
  }
}
