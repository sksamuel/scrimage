package com.sksamuel.scrimage

import com.sksamuel.scrimage.nio.JpegWriter
import org.apache.commons.io.IOUtils
import org.scalatest.{Matchers, WordSpec}

class IphoneOrientationTest extends WordSpec with Matchers {

  "iphone image" should {
    "be re-orientated" in {
      // Images from: https://github.com/recurser/exif-orientation-examples
      Image.fromResource("/com/sksamuel/scrimage/iphone/portrait_1.jpg").bytes(new JpegWriter(100, true)) shouldBe
        IOUtils.toByteArray(getClass.getResourceAsStream("/com/sksamuel/scrimage/iphone/portrait_1_expected.jpg"))

      Image.fromResource("/com/sksamuel/scrimage/iphone/portrait_2.jpg").bytes(new JpegWriter(100, true)) shouldBe
        IOUtils.toByteArray(getClass.getResourceAsStream("/com/sksamuel/scrimage/iphone/portrait_2_expected.jpg"))

      Image.fromResource("/com/sksamuel/scrimage/iphone/portrait_3.jpg").bytes(new JpegWriter(100, true)) shouldBe
        IOUtils.toByteArray(getClass.getResourceAsStream("/com/sksamuel/scrimage/iphone/portrait_3_expected.jpg"))

      Image.fromResource("/com/sksamuel/scrimage/iphone/portrait_4.jpg").bytes(new JpegWriter(100, true)) shouldBe
        IOUtils.toByteArray(getClass.getResourceAsStream("/com/sksamuel/scrimage/iphone/portrait_4_expected.jpg"))

      Image.fromResource("/com/sksamuel/scrimage/iphone/portrait_5.jpg").bytes(new JpegWriter(100, true)) shouldBe
        IOUtils.toByteArray(getClass.getResourceAsStream("/com/sksamuel/scrimage/iphone/portrait_5_expected.jpg"))

      Image.fromResource("/com/sksamuel/scrimage/iphone/portrait_6.jpg").bytes(new JpegWriter(100, true)) shouldBe
        IOUtils.toByteArray(getClass.getResourceAsStream("/com/sksamuel/scrimage/iphone/portrait_6_expected.jpg"))

      Image.fromResource("/com/sksamuel/scrimage/iphone/portrait_7.jpg").bytes(new JpegWriter(100, true)) shouldBe
        IOUtils.toByteArray(getClass.getResourceAsStream("/com/sksamuel/scrimage/iphone/portrait_7_expected.jpg"))

      Image.fromResource("/com/sksamuel/scrimage/iphone/portrait_8.jpg").bytes(new JpegWriter(100, true)) shouldBe
        IOUtils.toByteArray(getClass.getResourceAsStream("/com/sksamuel/scrimage/iphone/portrait_8_expected.jpg"));

      Image.fromResource("/com/sksamuel/scrimage/iphone/up.JPG").width shouldBe 1280
    }

    "orientate for multiple same-value tags #93" in {
      Image.fromResource("/issue93.jpg").width shouldBe 2160
    }
  }
}
