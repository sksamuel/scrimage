package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Regression: PixelateFilter forwarded blockSize to jhlabs BlockFilter, which
 * steps its loops by blockSize and allocates int[blockSize*blockSize].
 * blockSize == 0 spins forever (y += 0) and divides by zero; blockSize < 0
 * throws NegativeArraySizeException. The wrapper now validates blockSize >= 1
 * at construction (so an invalid value fails fast rather than hanging).
 */
class PixelateFilterValidationTest : FunSpec({

   test("PixelateFilter rejects blockSize == 0") {
      shouldThrow<IllegalArgumentException> { PixelateFilter(0) }
   }

   test("PixelateFilter rejects negative blockSize") {
      shouldThrow<IllegalArgumentException> { PixelateFilter(-3) }
   }

   test("PixelateFilter accepts the documented default and applies") {
      val image = ImmutableImage.filled(16, 16, Color(120, 120, 120))
      val result = image.filter(PixelateFilter())
      result.width shouldBe 16
      result.height shouldBe 16
   }
})
