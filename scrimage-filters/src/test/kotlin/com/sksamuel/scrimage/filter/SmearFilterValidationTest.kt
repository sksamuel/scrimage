package com.sksamuel.scrimage.filter

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

/**
 * Regression: SmearFilter accepted any (distance, density, mix). The
 * jhlabs implementation does `nextInt() % distance` in the
 * shape-drawing loops; distance == 0 throws ArithmeticException
 * ("/ by zero") deep inside filterPixels. density / mix outside [0,1]
 * have undefined semantics.
 */
class SmearFilterValidationTest : FunSpec({

   test("SmearFilter rejects distance == 0") {
      shouldThrow<IllegalArgumentException> { SmearFilter(SmearType.Lines, 0f, 0.3f, 0f, 0, 0.4f) }
   }

   test("SmearFilter rejects negative distance") {
      shouldThrow<IllegalArgumentException> { SmearFilter(SmearType.Lines, 0f, 0.3f, 0f, -1, 0.4f) }
   }

   test("SmearFilter rejects density > 1") {
      shouldThrow<IllegalArgumentException> { SmearFilter(SmearType.Lines, 0f, 1.5f, 0f, 3, 0.4f) }
   }

   test("SmearFilter rejects mix > 1") {
      shouldThrow<IllegalArgumentException> { SmearFilter(SmearType.Lines, 0f, 0.3f, 0f, 3, 1.5f) }
   }

   test("SmearFilter rejects null SmearType") {
      shouldThrow<IllegalArgumentException> { SmearFilter(null, 0f, 0.3f, 0f, 3, 0.4f) }
   }

   test("SmearFilter accepts documented defaults") {
      SmearFilter(SmearType.Lines)
      SmearFilter(SmearType.Circles, 0f, 0f, 0f, 1, 0f)
   }
})
