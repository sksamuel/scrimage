package com.sksamuel.scrimage.canvas

import com.sksamuel.scrimage.Image
import org.scalatest.{WordSpec, Matchers}

class WatermarkerTest extends WordSpec with Matchers {

  val image = Image.fromResource("/gibson.jpg")

  import Watermarker._

  "watermarker" should {
    "add repeated watermark" in {
      val marked = image.watermarker.repeated("watermark", TextStyle(size = 36))
      marked shouldBe Image.fromResource("/com/sksamuel/scrimage/canvas/watermarked_repeated.png")
    }
    "add centered watermark" in {
      val marked = image.watermarker.centered("watermark", TextStyle(size = 48, alpha = 0.2))
      marked shouldBe Image.fromResource("/com/sksamuel/scrimage/canvas/watermarked_centered.png")
    }
  }
}
