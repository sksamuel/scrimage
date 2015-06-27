package com.sksamuel.scrimage

import org.apache.commons.io.IOUtils
import org.scalatest.{WordSpec, Matchers}

class IphoneOrientationTest extends WordSpec with Matchers {

  "iphone image" should {
    "be re-orientated" in {
      Image.fromResource("/com/sksamuel/scrimage/iphone/left.JPG").scale(0.25).bytes shouldBe
        IOUtils.toByteArray(getClass.getResourceAsStream("/com/sksamuel/scrimage/iphone/left_reorientated.png"))
      Image.fromResource("/com/sksamuel/scrimage/iphone/left.JPG").width shouldBe 960

      Image.fromResource("/com/sksamuel/scrimage/iphone/down.JPG").scale(0.25).bytes shouldBe
        IOUtils.toByteArray(getClass.getResourceAsStream("/com/sksamuel/scrimage/iphone/down_reorientated.png"))
      Image.fromResource("/com/sksamuel/scrimage/iphone/down.JPG").width shouldBe 1280

      Image.fromResource("/com/sksamuel/scrimage/iphone/right.JPG").scale(0.25).bytes shouldBe
        IOUtils.toByteArray(getClass.getResourceAsStream("/com/sksamuel/scrimage/iphone/right_reorientated.png"))
      Image.fromResource("/com/sksamuel/scrimage/iphone/right.JPG").width shouldBe 960

      Image.fromResource("/com/sksamuel/scrimage/iphone/up.JPG").width shouldBe 1280
    }
  }
}
