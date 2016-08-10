package com.sksamuel.scrimage.canvas

import com.sksamuel.scrimage.Image
import org.scalatest.{Matchers, WordSpec}

class WatermarkTest extends WordSpec with Matchers {

  val image = Image.fromResource("/gibson.jpg")
  val font = Font.createTrueType(getClass.getResourceAsStream("/fonts/Roboto-Regular.ttf"))

  "watermarker" should {
    "add repeated watermark" in {
      val marked = image.filter(new WatermarkCoverFilter("watermark", size = 36, antiAlias = false, font = font))
      marked shouldBe Image.fromResource("/com/sksamuel/scrimage/canvas/watermarked_repeated.png")
    }
    "add stamped watermark" in {
      val marked = image.filter(new WatermarkStampFilter("watermark", size = 48, alpha = 0.2, antiAlias = false, font = font))
      marked shouldBe Image.fromResource("/com/sksamuel/scrimage/canvas/watermarked_centered.png")
    }
    "add located watermark" in {
      val marked = image.filter(new WatermarkFilter("watermark", 25, image.height - 100, size = 48, alpha = 0.2, antiAlias = false, font = font))
      marked shouldBe Image.fromResource("/com/sksamuel/scrimage/canvas/watermarked_at.png")
    }
  }
}
