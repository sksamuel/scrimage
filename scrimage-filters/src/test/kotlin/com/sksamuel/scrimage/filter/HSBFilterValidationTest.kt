package com.sksamuel.scrimage.filter

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

/**
 * Regression: HSBFilter advertised a [-1, 1] range for each factor but
 * enforced it with plain `assert`, which is a no-op without `-ea`. In
 * production a hue of 10 silently produced visually wrong output
 * rather than failing fast.
 */
class HSBFilterValidationTest : FunSpec({

   test("HSBFilter rejects hue > 1") {
      shouldThrow<IllegalArgumentException> { HSBFilter(10f, 0f, 0f) }
   }

   test("HSBFilter rejects hue < -1") {
      shouldThrow<IllegalArgumentException> { HSBFilter(-2f, 0f, 0f) }
   }

   test("HSBFilter rejects saturation > 1") {
      shouldThrow<IllegalArgumentException> { HSBFilter(0f, 2f, 0f) }
   }

   test("HSBFilter rejects brightness > 1") {
      shouldThrow<IllegalArgumentException> { HSBFilter(0f, 0f, 2f) }
   }

   test("HSBFilter rejects NaN") {
      shouldThrow<IllegalArgumentException> { HSBFilter(Float.NaN, 0f, 0f) }
   }

   test("HSBFilter accepts the documented range") {
      HSBFilter()
      HSBFilter(-1f, -1f, -1f)
      HSBFilter(1f, 1f, 1f)
      HSBFilter(0.3f, -0.2f, 0.5f)
   }
})
