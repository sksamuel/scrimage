package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.ScaleMethod
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Regression tests for operations on images with extreme aspect ratios.
 *
 * Two distinct failures used to crash these:
 *  - DimensionTools.dimensionsToFit floored the derived dimension to 0 (a 1x1000 image
 *    fitted into 100x100 produced a 0x100 target), so fit/max/bound threw
 *    "Error doing rescale. Target size was 0x100 but must be at least 3x3."
 *  - ResampleOp (the Bicubic/Bilinear/BSpline/Lanczos3 scaler) rejects targets smaller
 *    than 3x3 and sources smaller than the filter support, so even valid extreme targets
 *    (1x100) or sources (1px wide) crashed. resample() now falls back to nearest-neighbour
 *    for exactly those geometries.
 */
class ExtremeAspectRatioTest : FunSpec({

   val tall = ImmutableImage.create(1, 1000).fill(java.awt.Color.RED)

   test("fit handles extreme aspect ratios") {
      val fitted = tall.fit(100, 100)
      fitted.width shouldBe 100
      fitted.height shouldBe 100
   }

   test("max handles extreme aspect ratios") {
      val maxed = tall.max(100, 100)
      maxed.width shouldBe 1
      maxed.height shouldBe 100
   }

   test("bound handles extreme aspect ratios") {
      val bounded = tall.bound(100, 100)
      bounded.width shouldBe 1
      bounded.height shouldBe 100
   }

   test("cover handles sources narrower than the resample filter support") {
      val covered = tall.cover(100, 100)
      covered.width shouldBe 100
      covered.height shouldBe 100
      covered.pixel(50, 50).toARGBInt() shouldBe 0xFFFF0000.toInt()
   }

   test("scaleToWidth(1) does not throw for resample scale methods") {
      val image = ImmutableImage.create(100, 100).fill(java.awt.Color.BLUE)
      val scaled = image.scaleToWidth(1)
      scaled.width shouldBe 1
      scaled.height shouldBe 1
   }

   test("scale to tiny targets does not throw for resample scale methods") {
      val image = ImmutableImage.create(100, 100).fill(java.awt.Color.BLUE)
      val scaled = image.scale(0.02, ScaleMethod.Bicubic)
      scaled.width shouldBe 2
      scaled.height shouldBe 2
      scaled.pixel(1, 1).toARGBInt() shouldBe 0xFF0000FF.toInt()
   }
})
