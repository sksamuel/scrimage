package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class GaussianBlurFilterValidationTest : FunSpec({

   // Regression: a negative radius was accepted by the constructor and only
   // blew up at apply time, as a cryptic NegativeArraySizeException from
   // thirdparty GaussianFilter.makeKernel. It now fails fast in the
   // constructor with a clear IllegalArgumentException. Radius 0 is a safe
   // no-op and remains allowed.

   test("constructor rejects negative radius") {
      val ex = shouldThrow<IllegalArgumentException> {
         GaussianBlurFilter(-1)
      }
      ex.message!!.shouldContain("radius")
      ex.message!!.shouldContain("-1")
      shouldThrow<IllegalArgumentException> {
         GaussianBlurFilter(-10)
      }
   }

   test("radius 0 is a no-op and still applies cleanly") {
      val image = ImmutableImage.create(16, 16)
      image.filter(GaussianBlurFilter(0))
   }

   test("positive radius and default construction still apply cleanly") {
      val image = ImmutableImage.create(16, 16)
      image.filter(GaussianBlurFilter())
      image.filter(GaussianBlurFilter(3))
   }
})
