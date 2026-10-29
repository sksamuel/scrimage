package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.awt.Color

/**
 * Regression: GammaFilter accepted gamma <= 0 and NaN. The jhlabs
 * implementation builds its lookup table with Math.pow(i / 255.0, 1 / gamma),
 * so gamma 0 gives 1/0 = Infinity and every table entry collapses to 0: the
 * whole image came out black with no error. Negative gamma and NaN produced
 * an all-black (or garbage) table in the same silent way. The wrapper now
 * rejects NaN and gamma <= 0 at construction with an IllegalArgumentException.
 */
class GammaFilterValidationTest : FunSpec({

   test("GammaFilter rejects gamma 0") {
      val ex = shouldThrow<IllegalArgumentException> { GammaFilter(0.0) }
      ex.message!!.shouldContain("gamma")
   }

   test("GammaFilter rejects negative gamma") {
      shouldThrow<IllegalArgumentException> { GammaFilter(-1.5) }
   }

   test("GammaFilter rejects NaN gamma") {
      shouldThrow<IllegalArgumentException> { GammaFilter(Double.NaN) }
   }

   test("GammaFilter with a positive gamma still applies; gamma 1 is the identity") {
      val image = ImmutableImage.filled(4, 4, Color(100, 150, 200))
      image.filter(GammaFilter(1.0)).pixel(1, 1).toARGBInt() shouldBe image.pixel(1, 1).toARGBInt()
      image.filter(GammaFilter()).pixel(1, 1).toARGBInt() shouldBe image.pixel(1, 1).toARGBInt()
      image.filter(GammaFilter(2.0)).pixel(1, 1).red() shouldBe 160
   }
})
