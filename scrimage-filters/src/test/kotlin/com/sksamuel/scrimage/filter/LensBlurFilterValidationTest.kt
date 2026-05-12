package com.sksamuel.scrimage.filter

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

/**
 * Regression: LensBlurFilter accepted any (radius, sides). The jhlabs
 * implementation computes `polyAngle = π / sides` — with sides == 0
 * this divides by zero, and with sides < 3 produces a degenerate
 * "polygon" the bokeh-shape math doesn't handle. KaleidoscopeFilter
 * already validates sides ≥ 3 in this package; LensBlurFilter didn't.
 */
class LensBlurFilterValidationTest : FunSpec({

   test("LensBlurFilter rejects sides == 0") {
      shouldThrow<IllegalArgumentException> { LensBlurFilter(5f, 2f, 255f, 0) }
   }

   test("LensBlurFilter rejects sides < 3") {
      shouldThrow<IllegalArgumentException> { LensBlurFilter(5f, 2f, 255f, 2) }
   }

   test("LensBlurFilter rejects negative radius") {
      shouldThrow<IllegalArgumentException> { LensBlurFilter(-1f, 2f, 255f, 5) }
   }

   test("LensBlurFilter accepts the documented defaults") {
      LensBlurFilter()
      LensBlurFilter(0f, 2f, 255f, 3)
   }
})
