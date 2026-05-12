package com.sksamuel.scrimage.filter

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

/**
 * Regression: OilFilter accepted any (range, levels) and would crash
 * at filter-apply time with a NegativeArraySizeException (levels < 0),
 * ArrayIndexOutOfBoundsException (levels == 0, after the bucketing
 * loop), or silently waste a lot of memory (levels > 256, since the
 * bucketing math is `r * levels / 256`).
 *
 * Validate up front in the wrapper constructor.
 */
class OilFilterValidationTest : FunSpec({

   test("OilFilter rejects levels == 0") {
      shouldThrow<IllegalArgumentException> { OilFilter(3, 0) }
   }

   test("OilFilter rejects negative levels") {
      shouldThrow<IllegalArgumentException> { OilFilter(3, -1) }
   }

   test("OilFilter rejects levels > 256") {
      shouldThrow<IllegalArgumentException> { OilFilter(3, 257) }
   }

   test("OilFilter rejects negative range") {
      shouldThrow<IllegalArgumentException> { OilFilter(-1, 256) }
   }

   test("OilFilter accepts the documented defaults") {
      OilFilter()      // (3, 256)
      OilFilter(3, 1)  // tight
      OilFilter(3, 256)
   }
})
