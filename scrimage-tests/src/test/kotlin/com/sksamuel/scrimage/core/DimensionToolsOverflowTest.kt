package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.Dimension
import com.sksamuel.scrimage.DimensionTools
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Regression: dimensionsToFit compared `maxWidth * source.getY()` with
 * `maxHeight * source.getX()` using int multiplication. For very large
 * sources (e.g. a 60000 × 50000 tile) the products overflow int —
 * 60000 * 50000 = 3e9, well past Int.MAX_VALUE — and the sign flips,
 * selecting the wrong scaling branch.
 *
 * Promote to long for the comparison and for the result quotient
 * numerator.
 */
class DimensionToolsOverflowTest : FunSpec({

   test("dimensionsToFit on a 60000x50000 source picks the correct branch") {
      // Source 60000x50000, target 60000x60000.
      // Correctly: scale by min(60000/60000, 60000/50000) = 1.0, fit
      // result = (60000, 50000) since width is the constraint and
      // height is already smaller.
      // Pre-fix: `60000 * 50000` overflowed to negative, the < comparison
      // flipped, and the wrong branch was taken.
      val source = Dimension(60000, 50000)
      val target = Dimension(60000, 60000)
      val fit = DimensionTools.dimensionsToFit(target, source)
      fit.x shouldBe 60000
      fit.y shouldBe 50000
   }

   test("dimensionsToFit on a small image still works (regression check)") {
      // Sanity: small sources continue to work.
      val source = Dimension(400, 200)
      val target = Dimension(100, 100)
      val fit = DimensionTools.dimensionsToFit(target, source)
      // 400x200 fits inside 100x100 by scaling to 100x50 (aspect 2:1).
      fit.x shouldBe 100
      fit.y shouldBe 50
   }
})
