package com.sksamuel.scrimage.canvas

import com.sksamuel.scrimage.Image
import org.scalatest.{Matchers, WordSpec}

class DrawableStringTest extends WordSpec with Matchers {

  import Canvas._

  "Drawable String" should {
    "respect font override" in {
      val image = Image(400, 200).draw(DrawableString("Test", 20, 20, context = Context.Aliased, font = new java.awt.Font("default", 0, 24)))
      image.image shouldBe Image.fromResource("/com/sksamuel/scrimage/canvas/drawablestring.png")
    }
  }
}
