package com.sksamuel.scrimage

import org.scalatest.{Matchers, WordSpec}

class FormatDetectorTest extends WordSpec with Matchers {

  "format detector" should {
    "detect png" in {
      FormatDetector.detect(getClass.getResourceAsStream("/com/sksamuel/scrimage/chip_pad.png")).get shouldBe Format.PNG
    }
    "detect jpeg" in {
      FormatDetector.detect(getClass.getResourceAsStream("/com/sksamuel/scrimage/bird.jpg")).get shouldBe Format.JPEG
    }
    "detect gif" in {
      FormatDetector.detect(getClass.getResourceAsStream("/com/sksamuel/scrimage/io/bird_compressed.gif")).get shouldBe Format.GIF
    }
  }
}
