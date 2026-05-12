package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Regression test for Pixel.toAverageGrayscale().
 *
 * The method's name and javadoc both say "Average method takes the average
 * value of R, G, and B as the grayscale value". The previous implementation
 * actually called toColor().toGrayscale() which delegates to LumaGrayscale
 * (Rec. 709 weighted: 0.2126 R + 0.7152 G + 0.0722 B), not the arithmetic
 * mean.
 *
 * Concrete pre-fix behaviour for (r=100, g=150, b=200):
 *   - Documented:  (100 + 150 + 200) / 3 = 150
 *   - Old impl:    round(0.2126*100 + 0.7152*150 + 0.0722*200) = 143
 *
 * The new implementation matches the documentation by using the arithmetic
 * mean directly.
 */
class ToAverageGrayscaleTest : FunSpec({

   test("toAverageGrayscale on (100, 150, 200) returns the arithmetic mean 150") {
      val p = Pixel(0, 0, 100, 150, 200, 255)
      val gray = p.toAverageGrayscale()
      gray.red() shouldBe 150
      gray.green() shouldBe 150
      gray.blue() shouldBe 150
   }

   test("toAverageGrayscale preserves alpha") {
      val p = Pixel(0, 0, 100, 150, 200, 64)
      p.toAverageGrayscale().alpha() shouldBe 64
   }

   test("toAverageGrayscale preserves the pixel coordinates") {
      val p = Pixel(7, 11, 90, 90, 90, 255)
      val gray = p.toAverageGrayscale()
      gray.x shouldBe 7
      gray.y shouldBe 11
   }

   test("toAverageGrayscale of a grayscale pixel is the same pixel") {
      val p = Pixel(0, 0, 128, 128, 128, 255)
      val gray = p.toAverageGrayscale()
      gray.red() shouldBe 128
      gray.green() shouldBe 128
      gray.blue() shouldBe 128
   }

   test("toAverageGrayscale on pure red returns 85 (255/3)") {
      val p = Pixel(0, 0, 255, 0, 0, 255)
      // 255 / 3 = 85 (integer division)
      p.toAverageGrayscale().red() shouldBe 85
   }
})
