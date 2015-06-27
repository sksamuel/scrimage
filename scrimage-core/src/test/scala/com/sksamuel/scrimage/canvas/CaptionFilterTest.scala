package com.sksamuel.scrimage.canvas

import com.sksamuel.scrimage.{X11Colorlist, Image}
import org.scalatest.{Matchers, WordSpec}

class CaptionFilterTest extends WordSpec with Matchers {

  val image = Image.fromResource("/jpeg/fish.jpg")
  val font = Font.createTrueType(getClass.getResourceAsStream("/fonts/Roboto-Light.ttf"))

  "CaptionFilter" should {
    "place caption on image" in {
      image.filter(new CaptionFilter(
        "This is an example of a big scary mudsucking fish",
        padding = Padding(40, 40, 20, 20),
        textSize = 22,
        textAlpha = 1,
        antiAlias = false, // anti alias implementation is different on openjdk vs oraclejdk, so tests can't use it reliably
        font = font
      )) shouldBe Image.fromResource("/com/sksamuel/scrimage/canvas/fish_caption_bottom_left.png")
    }
    "place caption using full width" in {
      image.filter(new CaptionFilter(
        "This is an example of a big scary mudsucking fish",
        padding = Padding(40, 40, 20, 20),
        textSize = 22,
        textAlpha = 1,
        fullWidth = true,
        antiAlias = false,
        font = font
      )) shouldBe Image.fromResource("/com/sksamuel/scrimage/canvas/fish_caption_full_width.png")
    }
    "place caption using caption alpha and color" in {
      image.filter(new CaptionFilter(
        "This is an example of a big scary mudsucking fish",
        padding = Padding(40, 40, 20, 20),
        textSize = 22,
        captionAlpha = 0.4,
        captionBackground = X11Colorlist.Brown,
        antiAlias = false,
        textAlpha = 1,
        font = font
      )) shouldBe Image.fromResource("/com/sksamuel/scrimage/canvas/fish_caption_color_alpha.png")
    }
    "place caption using text alpha and color" in {
      image.filter(new CaptionFilter(
        "This is an example of a big scary mudsucking fish",
        padding = Padding(40, 40, 20, 20),
        textSize = 22,
        textAlpha = 0.8,
        antiAlias = false,
        textColor = X11Colorlist.CadetBlue4,
        font = font
      )) shouldBe Image.fromResource("/com/sksamuel/scrimage/canvas/fish_caption_text_color_alpha.png")
    }
  }
}
