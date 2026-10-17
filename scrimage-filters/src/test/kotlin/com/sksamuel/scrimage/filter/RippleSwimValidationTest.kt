package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.awt.Color
import java.util.Random

/**
 * Regression: RippleFilter accepted a zero (or negative / NaN) wavelength and
 * SwimFilter a zero stretch. The jhlabs implementations divide by these
 * values when computing the inverse transform (`y / xWavelength`,
 * `x / yWavelength`, `ny /= scale * stretch`), so every source coordinate
 * became NaN and the whole output was transparent black, with no error.
 * Both wrappers now reject non-positive (and NaN) values at construction.
 */
class RippleSwimValidationTest : FunSpec({

   test("RippleFilter rejects xWavelength <= 0") {
      val ex = shouldThrow<IllegalArgumentException> { RippleFilter(RippleType.Sine, 2f, 2f, 0f, 6f) }
      ex.message!!.shouldContain("xWavelength")
      shouldThrow<IllegalArgumentException> { RippleFilter(RippleType.Sine, 2f, 2f, -1f, 6f) }
      shouldThrow<IllegalArgumentException> { RippleFilter(RippleType.Sine, 2f, 2f, Float.NaN, 6f) }
   }

   test("RippleFilter rejects yWavelength <= 0") {
      val ex = shouldThrow<IllegalArgumentException> { RippleFilter(RippleType.Sine, 2f, 2f, 6f, 0f) }
      ex.message!!.shouldContain("yWavelength")
      shouldThrow<IllegalArgumentException> { RippleFilter(RippleType.Sawtooth, 2f, 2f, 6f, -3f) }
   }

   test("SwimFilter rejects stretch <= 0 in every constructor") {
      val ex = shouldThrow<IllegalArgumentException> { SwimFilter(6f, 0f) }
      ex.message!!.shouldContain("stretch")
      shouldThrow<IllegalArgumentException> { SwimFilter(Random(1), 6f, 0f) }
      shouldThrow<IllegalArgumentException> { SwimFilter(6f, -2f) }
      shouldThrow<IllegalArgumentException> { SwimFilter(6f, Float.NaN) }
   }

   test("valid parameters still apply and keep the image opaque") {
      val src = ImmutableImage.filled(8, 8, Color.RED)
      Integer.toHexString(src.filter(RippleFilter(RippleType.Sine, 2f, 2f, 6f, 6f)).pixel(4, 4).argb) shouldBe "ffff0000"
      Integer.toHexString(src.filter(RippleFilter(RippleType.Triangle)).pixel(4, 4).argb) shouldBe "ffff0000"
      Integer.toHexString(src.filter(SwimFilter(Random(1), 6f, 2f)).pixel(4, 4).argb) shouldBe "ffff0000"
      Integer.toHexString(src.filter(SwimFilter()).pixel(4, 4).argb) shouldBe "ffff0000"
   }
})
