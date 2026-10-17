package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Regression test for ImmutableImage.gamma(double) with a non-positive or NaN gamma.
 *
 * gamma() builds its lookup table from 1.0 / gamma: gamma = 0 gives Infinity (every
 * entry 0 or 255), a negative gamma gives negative exponents (table collapses), and NaN
 * gives NaN (every entry 0). The image silently became solid black or white instead of
 * failing fast. resizeToRatio in the same class already rejects NaN / <= 0 with an
 * IllegalArgumentException; gamma now does the same.
 */
class GammaValidationTest : FunSpec({

   fun gradient(): ImmutableImage {
      val img = ImmutableImage.create(8, 6)
      for (y in 0 until 6) for (x in 0 until 8) {
         img.awt().setRGB(x, y, Color((x * 30) % 256, (y * 40) % 256, ((x + y) * 10) % 256).rgb)
      }
      return img
   }

   test("gamma(0) throws IllegalArgumentException") {
      shouldThrow<IllegalArgumentException> { gradient().gamma(0.0) }
   }

   test("gamma(-1) throws IllegalArgumentException") {
      shouldThrow<IllegalArgumentException> { gradient().gamma(-1.0) }
   }

   test("gamma(NaN) throws IllegalArgumentException") {
      shouldThrow<IllegalArgumentException> { gradient().gamma(Double.NaN) }
   }

   test("gamma(1.0) is the identity") {
      val img = gradient()
      img.gamma(1.0).argbints().toList() shouldBe img.argbints().toList()
   }

   test("a small positive gamma is still accepted") {
      val img = gradient()
      val out = img.gamma(0.1)
      out.width shouldBe 8
      out.height shouldBe 6
   }
})
