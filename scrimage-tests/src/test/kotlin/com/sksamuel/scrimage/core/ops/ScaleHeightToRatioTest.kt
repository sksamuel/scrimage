package com.sksamuel.scrimage.core.ops

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Regression: scaleHeightToRatio computed the new height from
 * `width * ratio` instead of `height * ratio`. On a non-square image
 * the result therefore depended on the wrong dimension and produced
 * an unrelated height. For example, on a 400 x 200 image,
 * `scaleHeightToRatio(0.5)` returned an image of height 200 (= width
 * * 0.5) — completely unchanged — rather than the expected height 100.
 */
class ScaleHeightToRatioTest : FunSpec({

   test("scaleHeightToRatio(0.5) halves the height of a non-square image") {
      val img = ImmutableImage.create(400, 200)
      val scaled = img.scaleHeightToRatio(0.5)
      scaled.height shouldBe 100
   }

   test("scaleHeightToRatio(2.0) doubles the height of a non-square image") {
      val img = ImmutableImage.create(400, 200)
      val scaled = img.scaleHeightToRatio(2.0)
      scaled.height shouldBe 400
   }

   test("scaleHeightToRatio(1.0) preserves the height") {
      val img = ImmutableImage.create(400, 200)
      img.scaleHeightToRatio(1.0).height shouldBe 200
   }
})
