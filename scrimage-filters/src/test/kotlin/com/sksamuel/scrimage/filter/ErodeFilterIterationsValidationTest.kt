package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.awt.Color

/**
 * Regression: ErodeFilter accepted iterations <= 0. The jhlabs implementation
 * allocates a fresh int[] for the output and only fills it inside the
 * iterations loop, so ErodeFilter(0) silently returned a fully transparent
 * black image instead of the (unchanged) input. It now fails fast in the
 * constructor with an IllegalArgumentException.
 */
class ErodeFilterIterationsValidationTest : FunSpec({

   test("ErodeFilter rejects 0 iterations") {
      val ex = shouldThrow<IllegalArgumentException> { ErodeFilter(0) }
      ex.message!!.shouldContain("iterations")
      ex.message!!.shouldContain("0")
   }

   test("ErodeFilter rejects negative iterations") {
      shouldThrow<IllegalArgumentException> { ErodeFilter(-3) }
   }

   test("ErodeFilter with a positive iteration count still applies and keeps pixels opaque") {
      val out = ImmutableImage.filled(5, 5, Color.RED).filter(ErodeFilter(1))
      Integer.toHexString(out.pixel(2, 2).argb) shouldBe "ffff0000"
      ImmutableImage.filled(5, 5, Color.RED).filter(ErodeFilter()).width shouldBe 5
   }
})
