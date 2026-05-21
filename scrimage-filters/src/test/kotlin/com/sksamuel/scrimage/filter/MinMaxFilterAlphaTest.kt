package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Regression: MaximumFilter / MinimumFilter seeded their per-pixel
 * accumulator with 0xff000000 / 0xffffffff. PixelUtils.combinePixels
 * with op=MAX/MIN only updates the RGB channels and keeps the
 * accumulator's alpha, so every output pixel had alpha 0xff regardless
 * of the source. Transparent regions came out fully opaque.
 *
 * The fix seeds the accumulator with the centre pixel (which the
 * inner 3x3 loop already visits via dx=dy=0), preserving its alpha.
 */
class MinMaxFilterAlphaTest : FunSpec({

   // Build a 3x3 image where the centre pixel is half-transparent
   // and the others are fully opaque.
   fun checker(): ImmutableImage {
      val pixels = Array(9) { i ->
         val x = i % 3
         val y = i / 3
         val alpha = if (x == 1 && y == 1) 64 else 255
         Pixel(x, y, 100, 100, 100, alpha)
      }
      return ImmutableImage.create(3, 3, pixels)
   }

   test("MaximumFilter preserves the centre pixel's source alpha") {
      val out = checker().filter(MaximumFilter())
      // Pre-fix: alpha was forced to 0xff for every pixel.
      // Post-fix: the centre pixel (1,1) retains its source alpha = 64.
      out.pixel(1, 1).alpha() shouldBe 64
   }

   test("MinimumFilter preserves the centre pixel's source alpha") {
      val out = checker().filter(MinimumFilter())
      out.pixel(1, 1).alpha() shouldBe 64
   }

   test("MaximumFilter still correctly maxes neighbour RGB") {
      // Centre is dark, neighbour brighter. Output centre RGB should
      // equal the maxed neighbour's RGB.
      val pixels = arrayOf(
         Pixel(0, 0, 100, 100, 100, 255),
         Pixel(1, 0, 200, 200, 200, 255),
         Pixel(2, 0, 100, 100, 100, 255),
         Pixel(0, 1, 100, 100, 100, 255),
         Pixel(1, 1, 50, 50, 50, 255),
         Pixel(2, 1, 100, 100, 100, 255),
         Pixel(0, 2, 100, 100, 100, 255),
         Pixel(1, 2, 100, 100, 100, 255),
         Pixel(2, 2, 100, 100, 100, 255)
      )
      val out = ImmutableImage.create(3, 3, pixels).filter(MaximumFilter())
      out.pixel(1, 1).red() shouldBe 200
   }
})
