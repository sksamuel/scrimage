package com.sksamuel.scrimage.filter

import java.awt.Color

import com.sksamuel.scrimage.{FontUtils, Image}
import com.sksamuel.scrimage.nio.PngWriter
import org.scalatest.{Matchers, WordSpec}

class WatermarkTest extends WordSpec with Matchers {

  implicit private val writer: PngWriter = PngWriter.MaxCompression
  private val image = Image.fromResource("/gibson.jpg")

  "watermarker" should {
    "add repeated watermark" ignore {
      val font = FontUtils.createTrueType(getClass.getResourceAsStream("/fonts/Roboto-Regular.ttf"), 36)
      val marked = image.filter(new WatermarkCoverFilter("watermark", font, false, 0.1, Color.WHITE))
      marked shouldBe Image.fromResource("/com/sksamuel/scrimage/filters/watermark_cover.png")
    }
    "add stamped watermark" ignore {
      val font = FontUtils.createTrueType(getClass.getResourceAsStream("/fonts/Roboto-Regular.ttf"), 48)
      val marked = image.filter(new WatermarkStampFilter("watermark", font, false, 0.2, Color.WHITE))
      marked shouldBe Image.fromResource("/com/sksamuel/scrimage/filters/watermark_stamp.png")
    }
    "add located watermark" ignore {
      val font = FontUtils.createTrueType(getClass.getResourceAsStream("/fonts/Roboto-Regular.ttf"), 48)
      val marked = image.filter(new WatermarkFilter("watermark", 25, image.height - 100, font, false, 0.2, Color.WHITE))
      marked shouldBe Image.fromResource("/com/sksamuel/scrimage/filters/watermark.png")
    }
  }
}
