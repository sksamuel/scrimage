package com.sksamuel.scrimage.canvas

import com.sksamuel.scrimage.Image
import org.scalatest.{WordSpec, Matchers}

class WatermarkerText extends WordSpec with Matchers {

  val image = Image.fromResource("/gibson.jpg")

  import Watermarker._

  "watermarker" should {
    "add repeated watermark" in {
      val marked = image.watermarker.repeated("watermark", TextStyle(size = 36))
      marked.output("watermarked_repeated.png")
    }
  }
}
