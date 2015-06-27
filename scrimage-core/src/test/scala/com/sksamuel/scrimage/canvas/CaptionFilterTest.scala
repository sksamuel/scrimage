package com.sksamuel.scrimage.canvas

import com.sksamuel.scrimage.Image
import org.scalatest.{Matchers, WordSpec}

class CaptionFilterTest extends WordSpec with Matchers {

  val image = Image.fromResource("/jpeg/fish.jpg")

  "CaptionFilter" should {
    "place caption on image" in {
      val font = Font.createTrueType(getClass.getResourceAsStream("/fonts/Roboto-Light.ttf"))
      image.filter(
        new CaptionFilter(
          "This is an example of a big scary mudsucking fish",
          padding = Padding(40, 40, 20, 20),
          size = 22,
          textAlpha = 1,
          font = font
        )
      ).output("fish_caption.png")
    }
  }
}
