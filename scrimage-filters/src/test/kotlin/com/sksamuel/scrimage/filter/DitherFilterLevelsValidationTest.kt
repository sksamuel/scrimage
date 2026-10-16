package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import java.awt.Color

/**
 * Regression: DitherFilter accepted any levels value and failed later inside the jhlabs filter,
 * which builds a lookup table with `255 * i / (levels - 1)` and `new int[levels]`:
 * DitherFilter(1) threw ArithmeticException (/ by zero) at apply time, DitherFilter(0) threw
 * ArrayIndexOutOfBoundsException and negative values NegativeArraySizeException. The constructor
 * now rejects levels < 2 up front.
 */
class DitherFilterLevelsValidationTest : FunSpec({

   val src = ImmutableImage.filled(16, 16, Color(90, 140, 200))

   test("constructor rejects levels of 1") {
      val ex = shouldThrow<IllegalArgumentException> { DitherFilter(1) }
      ex.message!!.shouldContain("levels")
      ex.message!!.shouldContain("1")
   }

   test("constructor rejects levels of 0") {
      shouldThrow<IllegalArgumentException> { DitherFilter(0) }
   }

   test("constructor rejects negative levels") {
      shouldThrow<IllegalArgumentException> { DitherFilter(-3) }
   }

   test("levels of 2 is the smallest valid value and applies cleanly") {
      shouldNotThrowAny { src.filter(DitherFilter(2)) }
   }
})
