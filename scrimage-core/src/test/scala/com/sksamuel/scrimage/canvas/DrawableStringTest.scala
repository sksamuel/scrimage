package com.sksamuel.scrimage.canvas

import com.sksamuel.scrimage.Image
import org.scalatest.{Matchers, WordSpec}

class DrawableStringTest extends WordSpec with Matchers {

  import Canvas._

  "Drawable String" should {
    "respect font override" in {
      val image = Image(400, 200).draw(DrawableString("Test", 20, 20, font = new java.awt.Font("default", 0, 24)))
      image.output("drawablestring.png")
      image.hashCode shouldBe Image.fromResource("/com/sksamuel/scrimage/canvas/drawablestring.png").hashCode
    }
  }
}
