package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Regression: NoiseReduction crashed with ArrayIndexOutOfBoundsException on a
 * 1px-wide or 1px-tall image. The total-variation derivative helpers
 * (diff_x/diff_y/diff_xx/diff_yy/diff_xy) index [i+1] / [j+1], which is out of
 * bounds when width or height is 1. Such an image has no 2D neighbourhood to
 * denoise, so it is now copied through unchanged. The filter runs the helpers
 * along both axes, so both orientations crashed.
 */
class NoiseReductionFilterTinyImageTest : FunSpec({

   fun image(width: Int, height: Int): ImmutableImage =
      ImmutableImage.filled(width, height, Color.ORANGE)

   test("noise reduction of a 1x1 image completes and preserves dimensions") {
      val result = image(1, 1).filter(NoiseReductionFilter())
      result.width shouldBe 1
      result.height shouldBe 1
   }

   test("noise reduction of a 1xN image completes and preserves dimensions") {
      val result = image(1, 7).filter(NoiseReductionFilter())
      result.width shouldBe 1
      result.height shouldBe 7
   }

   test("noise reduction of an Nx1 image completes and preserves dimensions") {
      val result = image(7, 1).filter(NoiseReductionFilter())
      result.width shouldBe 7
      result.height shouldBe 1
   }

   test("noise reduction of a 2x2 image completes and preserves dimensions") {
      val result = image(2, 2).filter(NoiseReductionFilter())
      result.width shouldBe 2
      result.height shouldBe 2
   }
})
