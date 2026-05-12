package com.sksamuel.scrimage.filter

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class BlackThresholdFilterRangeTest : FunSpec({

   // Regression: BlackThresholdFilter validated thresholdPercentage in
   // [0, 100] via `assert`, which is disabled by default in production
   // JVMs. Out-of-range values then silently produced wrong output:
   //   - pct > 100  → threshold > 255, every pixel is below it, image
   //                  comes out fully blackened
   //   - pct < 0    → threshold < 0,  no pixel is below it, filter is
   //                  a silent no-op
   //   - pct = NaN  → threshold = (int) NaN = 0, same silent no-op

   test("constructor rejects negative threshold") {
      val ex = shouldThrow<IllegalArgumentException> { BlackThresholdFilter(-10.0) }
      ex.message!!.shouldContain("thresholdPercentage")
      ex.message!!.shouldContain("-10")
   }

   test("constructor rejects threshold above 100") {
      val ex = shouldThrow<IllegalArgumentException> { BlackThresholdFilter(150.0) }
      ex.message!!.shouldContain("thresholdPercentage")
      ex.message!!.shouldContain("150")
   }

   test("constructor rejects NaN") {
      val ex = shouldThrow<IllegalArgumentException> { BlackThresholdFilter(Double.NaN) }
      ex.message!!.shouldContain("thresholdPercentage")
   }

   test("constructor accepts boundary values 0 and 100") {
      BlackThresholdFilter(0.0)
      BlackThresholdFilter(100.0)
   }
})
