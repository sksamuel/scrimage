package com.sksamuel.scrimage.core.ops

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.ScaleMethod
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Regression test for the factor-based scale/resize/zoom/scaleHeightToRatio overloads
 * with a small factor.
 *
 * `(int) (width * factor)` floors to 0 for a small factor (e.g. 5 * 0.01), which then
 * fails inside AWT with "Width (0) and height (0) cannot be <= 0". The dimension-based
 * overloads (scaleToWidth/Height, resizeToWidth/Height, DimensionTools) already clamp
 * derived dimensions to at least 1; the factor overloads now do the same.
 */
class ScaleFactorMinDimensionTest : FunSpec({

   test("scale(0.01) on a 5x5 image clamps to 1x1 rather than crashing") {
      val out = ImmutableImage.create(5, 5).scale(0.01)
      out.width shouldBe 1
      out.height shouldBe 1
   }

   test("scale(0.01, FastScale) on a 5x5 image clamps to 1x1") {
      val out = ImmutableImage.create(5, 5).scale(0.01, ScaleMethod.FastScale)
      out.width shouldBe 1
      out.height shouldBe 1
   }

   test("scale with a factor that floors one dimension to 0 clamps only that dimension") {
      val out = ImmutableImage.create(100, 5).scale(0.1)
      out.width shouldBe 10
      out.height shouldBe 1
   }

   test("resize(0.01) on a 5x5 image clamps to a 1x1 canvas rather than crashing") {
      val out = ImmutableImage.create(5, 5).resize(0.01)
      out.width shouldBe 1
      out.height shouldBe 1
   }

   test("zoom(0.01) on a 5x5 image keeps the canvas size rather than crashing") {
      val out = ImmutableImage.create(5, 5).zoom(0.01)
      out.width shouldBe 5
      out.height shouldBe 5
   }

   test("scaleHeightToRatio(0.01) on a 5x5 image clamps the height to 1 rather than crashing") {
      val out = ImmutableImage.create(5, 5).scaleHeightToRatio(0.01)
      out.height shouldBe 1
      out.width shouldBe 1
   }

   test("normal factors are unaffected") {
      ImmutableImage.create(100, 50).scale(0.5).width shouldBe 50
      ImmutableImage.create(100, 50).scale(0.5).height shouldBe 25
      ImmutableImage.create(100, 50).resize(0.5).width shouldBe 50
      ImmutableImage.create(100, 50).scaleHeightToRatio(0.5).height shouldBe 25
   }
})
