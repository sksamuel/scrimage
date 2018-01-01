package com.sksamuel.scrimage.filter

import java.awt.Color

import com.sksamuel.scrimage.{FontUtils, Image}
import com.sksamuel.scrimage.nio.PngWriter
import org.scalatest.{Matchers, WordSpec}

class WatermarkTest extends WordSpec with Matchers {

  implicit private val writer: PngWriter = PngWriter.MaxCompression
  private val image = Image.fromResource("/gibson.jpg")

  "watermarker" should {
    "add repeated watermark" in {
      val font = FontUtils.createTrueType(getClass.getResourceAsStream("/fonts/Roboto-Regular.ttf"), 36)
      val marked = image.filter(new WatermarkCoverFilter("watermark", font, false, 0.1, Color.WHITE))
      marked shouldBe Image.fromResource("/com/sksamuel/scrimage/filters/watermark_cover.png")
    }
    "add stamped watermark" in {
      val font = FontUtils.createTrueType(getClass.getResourceAsStream("/fonts/Roboto-Regular.ttf"), 48)
      val marked = image.filter(new WatermarkStampFilter("watermark", font, false, 0.2, Color.WHITE))
      marked shouldBe Image.fromResource("/com/sksamuel/scrimage/filters/watermark_stamp.png")
    }
    "add located watermark" in {
      val font = FontUtils.createTrueType(getClass.getResourceAsStream("/fonts/Roboto-Regular.ttf"), 25)
      val marked = image.filter(new WatermarkFilter("watermark", image.height - 100, 48, font, false, 0.2, Color.WHITE))
      marked shouldBe Image.fromResource("/com/sksamuel/scrimage/filters/watermark.png")
    }
  }
}
