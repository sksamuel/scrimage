package com.sksamuel.scrimage.filter

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

/**
 * Regression: ColorHalftoneFilter and CrystallizeFilter accepted
 * non-positive sizing parameters that crashed (div-by-zero) or
 * silently produced garbage output in the jhlabs implementations.
 *
 * - ColorHalftoneFilter: gridSize = 2 * radius * √2; radius == 0
 *   makes gridSize == 0 and ImageMath.mod divides by zero.
 * - CrystallizeFilter: f = (f2 - f1) / edgeThickness; edgeThickness
 *   == 0 produces ±Infinity and the smoothStep flattens the image.
 */
class HalftoneCrystallizeValidationTest : FunSpec({

   test("ColorHalftoneFilter rejects radius == 0") {
      shouldThrow<IllegalArgumentException> { ColorHalftoneFilter(0f) }
   }

   test("ColorHalftoneFilter rejects negative radius") {
      shouldThrow<IllegalArgumentException> { ColorHalftoneFilter(-1f) }
   }

   test("ColorHalftoneFilter accepts the documented default") {
      ColorHalftoneFilter()
   }

   test("CrystallizeFilter rejects scale == 0") {
      shouldThrow<IllegalArgumentException> { CrystallizeFilter(0.0, 0.4, 0xff000000.toInt(), 0.2) }
   }

   test("CrystallizeFilter rejects edgeThickness == 0") {
      shouldThrow<IllegalArgumentException> { CrystallizeFilter(16.0, 0.0, 0xff000000.toInt(), 0.2) }
   }

   test("CrystallizeFilter rejects randomness > 1") {
      shouldThrow<IllegalArgumentException> { CrystallizeFilter(16.0, 0.4, 0xff000000.toInt(), 1.5) }
   }

   test("CrystallizeFilter accepts the documented default") {
      CrystallizeFilter()
   }
})
