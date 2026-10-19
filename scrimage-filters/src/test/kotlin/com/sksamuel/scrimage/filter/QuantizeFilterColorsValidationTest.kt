package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.awt.Color

/**
 * The jhlabs QuantizeFilter clamps the requested colour count to [8, 256]
 * (`setNumColors` does `Math.min(Math.max(numColors, 8), 256)`), so
 * QuantizeFilter(2) actually quantizes to up to 8 colours. That clamp is now
 * documented on the wrapper rather than changed (throwing would break
 * existing callers). Only a nonsensical count (< 1) is rejected up front.
 */
class QuantizeFilterColorsValidationTest : FunSpec({

   fun gradient(w: Int, h: Int): ImmutableImage {
      val img = ImmutableImage.create(w, h)
      for (y in 0 until h) for (x in 0 until w) {
         img.awt().setRGB(x, y, Color((x * 20) % 256, (y * 25) % 256, (x + y) * 10 % 256).rgb)
      }
      return img
   }

   test("QuantizeFilter rejects colors < 1") {
      val ex = shouldThrow<IllegalArgumentException> { QuantizeFilter(0) }
      ex.message!!.shouldContain("colors")
      shouldThrow<IllegalArgumentException> { QuantizeFilter(-5, true) }
      shouldThrow<IllegalArgumentException> { QuantizeFilter(0, true, false) }
   }

   test("QuantizeFilter(2) does not throw and is clamped to at most 8 colours") {
      val out = gradient(40, 40).filter(QuantizeFilter(2))
      out.width shouldBe 40
      out.argbints().toSet().size shouldBeLessThanOrEqual 8
   }

   test("QuantizeFilter(1000) does not throw and is clamped to at most 256 colours") {
      val out = gradient(40, 40).filter(QuantizeFilter(1000))
      out.argbints().toSet().size shouldBeLessThanOrEqual 256
   }
})
