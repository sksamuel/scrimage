package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage._
import com.sksamuel.scrimage.nio.PngWriter
import org.scalatest.{Matchers, WordSpec}

class CaptionFilterTest extends WordSpec with Matchers {

  implicit private val writer: PngWriter = PngWriter.MaxCompression
  private val image = ImmutableImage.fromResource("/fish.jpg")
  private val font = FontUtils.createTrueType(getClass.getResourceAsStream("/fonts/Roboto-Light.ttf"), 12)

  // anti alias implementation is different on openjdk vs oraclejdk, so tests can't use it reliably
  val alias = false

  "CaptionFilter" should {
    "place caption on image" ignore {
      val q = image.filter(new CaptionFilter(
        "This is an example of a big scary mudsucking fish",
        Position.BottomLeft,
        font,
        Color.White.toAWT,
        1,
        false,
        false,
        Color.White.toAWT,
        0.1,
        new Padding(40, 40, 20, 20)
      ))
      q shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/fish_caption_bottom_left.png")
    }
    "place caption using full width" ignore {
      val q = image.filter(new CaptionFilter(
        "This is an example of a big scary mudsucking fish",
        Position.BottomLeft,
        font,
        Color.White.toAWT,
        1,
        true,
        true,
        Color.White.toAWT,
        0.1,
        new Padding(40, 40, 20, 20)
      ))
      q shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/fish_caption_full_width.png")
    }
    "place caption using caption alpha and color" ignore {
      val q = image.filter(new CaptionFilter(
        "This is an example of a big scary mudsucking fish",
        Position.BottomLeft,
        font,
        Color.White.toAWT,
        1,
        true,
        false,
        X11Colorlist.Brown.toAWT,
        0.4,
        new Padding(40, 40, 20, 20)
      ))
      q shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/fish_caption_color_alpha.png")
    }
    "place caption using text alpha and color" ignore {
      val q = image.filter(new CaptionFilter(
        "This is an example of a big scary mudsucking fish",
        Position.BottomLeft,
        font,
        X11Colorlist.CadetBlue4.toAWT,
        0.8,
        false,
        false,
        Color.White.toAWT,
        0.1,
        new Padding(40, 40, 20, 20)
      ))
      q shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/fish_caption_text_color_alpha.png")
    }
    "allow setting size" ignore {
      val q = image.filter(new CaptionFilter(
        "This is an example of a big scary mudsucking fish",
        Position.BottomLeft,
        font.deriveFont(50f),
        X11Colorlist.CadetBlue4.toAWT,
        0.8,
        false,
        false,
        Color.White.toAWT,
        0.1,
        new Padding(40, 40, 20, 20)
      ))
      q shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/fish_caption_font_size.png")
    }
    "place caption using anti alias" ignore {
      val q = image.filter(new CaptionFilter(
        "This is an example of a big scary mudsucking fish",
        Position.BottomLeft,
        font,
        Color.White.toAWT,
        1.0,
        true,
        false,
        Color.White.toAWT,
        0.1,
        new Padding(40, 40, 20, 20)
      ))
      q shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/fish_caption_anti_alias.png")
    }
  }
}
