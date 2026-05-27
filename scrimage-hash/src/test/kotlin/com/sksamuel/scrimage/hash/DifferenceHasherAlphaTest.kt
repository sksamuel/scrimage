package com.sksamuel.scrimage.hash

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Regression: DifferenceHasher compared packed ARGB ints with signed
 * `<`. Opaque grayscale pixels have alpha bits `0xFF`, making the int
 * negative — two opaque pixels of different brightness still compare
 * correctly because the sign bit cancels, but as soon as the source
 * image has any per-pixel alpha variation the hash sorts by alpha
 * (top byte dominates the comparison) instead of brightness.
 *
 * The fix compares the unsigned red channel of the already-grayscale
 * image, which is brightness.
 */
class DifferenceHasherAlphaTest : FunSpec({

   // Build a 9x8 image whose left-to-right gradient is monotonically
   // increasing in brightness *and* the leftmost column happens to
   // have a translucent strip. After toGrayscale + scaleTo the small
   // image has 8 rows x 9 columns; the dhash compares adjacent
   // columns within each row → 8 bits per row, 64 bits total.
   //
   // The hash should reflect brightness ordering ("each pair to the
   // right is brighter than the one to the left"), regardless of
   // whether the source had alpha variation.
   test("dhash with translucent strips still reflects brightness ordering") {
      // 16 wide gradient, rows uniform. Left half opaque, right half opaque.
      // Translucent strip in the leftmost column doesn't carry brightness
      // information but used to dominate the signed argb compare.
      val pixels = Array(16 * 8) { i ->
         val x = i % 16
         val y = i / 16
         // Brightness increases L→R.
         val luma = x * 16
         // Leftmost column 50% transparent; rest opaque. The argb int
         // becomes positive-or-negative depending on alpha, which is
         // what the bug latched onto.
         val alpha = if (x == 0) 128 else 255
         Pixel(x, y, luma, luma, luma, alpha)
      }
      val image = ImmutableImage.create(16, 8, pixels)

      val hash = image.dhash()

      // Each "1" bit means the left pixel was less bright (= "dimmer").
      // For a strictly increasing brightness gradient, every adjacent
      // pair should yield 1. Pre-fix, the leftmost comparison was
      // skewed by the alpha discontinuity and the hash had a leading
      // 0 where every other position was 1.
      hash.size shouldBe 8 * 8
      // All bits should be 1 (each next column is brighter), regardless
      // of alpha variation in the source.
      hash.all { it == 1 } shouldBe true
   }
})
