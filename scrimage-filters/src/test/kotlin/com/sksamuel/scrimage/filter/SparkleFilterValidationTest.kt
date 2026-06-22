package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Regression: SparkleFilter passed `rays` straight to jhlabs SparkleFilter,
 * which indexes `rayLengths[i % rays]` for every pixel. rays == 0 threw
 * ArithmeticException (/ by zero) on the first pixel, and rays < 0 allocated
 * a negative-length array. The wrapper now validates rays >= 1.
 */
class SparkleFilterValidationTest : FunSpec({

   test("SparkleFilter rejects rays == 0") {
      shouldThrow<IllegalArgumentException> { SparkleFilter(0, 0, 0, 25, 50) }
   }

   test("SparkleFilter rejects negative rays") {
      shouldThrow<IllegalArgumentException> { SparkleFilter(0, 0, -1, 25, 50) }
   }

   test("SparkleFilter accepts the documented defaults and applies") {
      val image = ImmutableImage.filled(16, 16, Color(120, 120, 120))
      val result = image.filter(SparkleFilter())
      result.width shouldBe 16
      result.height shouldBe 16
   }
})
