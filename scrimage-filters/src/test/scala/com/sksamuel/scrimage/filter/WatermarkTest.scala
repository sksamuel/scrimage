package com.sksamuel.scrimage.filter

import java.awt.Color

import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.canvas.Font
import com.sksamuel.scrimage.nio.PngWriter
import org.scalatest.{Matchers, WordSpec}

class WatermarkTest extends WordSpec with Matchers {

  implicit private val writer = PngWriter.MaxCompression
  private val image = Image.fromResource("/gibson.jpg")
  private val font = Font.createTrueType(getClass.getResourceAsStream("/fonts/Roboto-Regular.ttf"))

  "watermarker" should {
    "add repeated watermark" in {
      val marked = image.filter(new WatermarkCoverFilter("watermark", 36, font, false, 0.1, Color.WHITE))
      marked shouldBe Image.fromResource("/com/sksamuel/scrimage/filters/watermark_cover.png")
    }
    "add stamped watermark" in {
      val marked = image.filter(new WatermarkStampFilter("watermark", 48, font, false, 0.2, Color.WHITE))
      marked shouldBe Image.fromResource("/com/sksamuel/scrimage/filters/watermark_stamp.png")
    }
    "add located watermark" in {
      val marked = image.filter(new WatermarkFilter("watermark", 25, image.height - 100, 48, font, false, 0.2, Color.WHITE))
      marked shouldBe Image.fromResource("/com/sksamuel/scrimage/filters/watermark.png")
    }
  }
}
