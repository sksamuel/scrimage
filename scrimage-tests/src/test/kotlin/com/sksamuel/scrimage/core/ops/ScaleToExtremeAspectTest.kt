package com.sksamuel.scrimage.core.ops

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.ScaleMethod
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Regression for scaleToWidth / scaleToHeight deriving a 0 dimension on an extreme
 * aspect ratio. The derived dimension is computed with integer truncation, so e.g.
 * scaleToWidth(1) on a 1000x1 image floored the height to 0 and then threw creating
 * a 0-height image. The derived dimension is now clamped to at least 1, mirroring the
 * existing clamp in resizeToWidth / resizeToHeight.
 *
 * FastScale is used because the bicubic/bilinear resamplers impose their own minimum
 * target size (3x3); FastScale (nearest neighbour) has no such floor, so it isolates
 * the derived-dimension clamp.
 */
class ScaleToExtremeAspectTest : FunSpec({

   test("scaleToWidth clamps the derived height to at least 1") {
      val image = ImmutableImage.create(1000, 1)
      val scaled = image.scaleToWidth(1, ScaleMethod.FastScale)
      scaled.width shouldBe 1
      scaled.height shouldBe 1
   }

   test("scaleToHeight clamps the derived width to at least 1") {
      val image = ImmutableImage.create(1, 1000)
      val scaled = image.scaleToHeight(1, ScaleMethod.FastScale)
      scaled.width shouldBe 1
      scaled.height shouldBe 1
   }
})
