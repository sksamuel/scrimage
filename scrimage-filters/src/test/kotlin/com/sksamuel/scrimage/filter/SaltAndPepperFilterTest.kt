package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import java.awt.Color

class SaltAndPepperFilterTest : FunSpec({

   // With salt=1.0 and pepper=0.0, the inner branch reached for every pixel
   // is the salt path. After the filter, every pixel should be opaque white.
   //
   // Previously the salt branch packed `(255 << 16) | (255 << 8) | 255`
   // (= 0x00FFFFFF) as the ARGB value, so on a TYPE_INT_ARGB image every
   // "white" pixel had alpha=0 and the entire image came out fully
   // transparent.
   test("salt produces opaque white pixels on a TYPE_INT_ARGB image") {
      val source = ImmutableImage.filled(40, 30, Color.RED)
      val out = source.filter(SaltAndPepperFilter(1.0, 0.0))

      // Every pixel must now be opaque white. The earlier behaviour gave
      // alpha=0 for every salt pixel.
      out.pixels().forEach { p ->
         p.alpha() shouldBe 255
         p.red() shouldBe 255
         p.green() shouldBe 255
         p.blue() shouldBe 255
      }
   }

   // Symmetric coverage for the pepper path: with pepper=1.0 every pixel
   // should be opaque black, not transparent black.
   test("pepper produces opaque black pixels on a TYPE_INT_ARGB image") {
      val source = ImmutableImage.filled(40, 30, Color.RED)
      val out = source.filter(SaltAndPepperFilter(0.0, 1.0))

      out.pixels().forEach { p ->
         p.alpha() shouldBe 255
         p.red() shouldBe 0
         p.green() shouldBe 0
         p.blue() shouldBe 0
      }
   }

   // Regression: salt and pepper probabilities were not independent.
   // The old code drew two independent Math.random() calls in an
   // if/else-if, so the salt branch was reached only when the pepper
   // branch did not fire — effective salt probability was
   // (1 - pepper) * salt instead of salt. For salt=pepper=0.5 the
   // distribution was 50% pepper / 25% salt / 25% unchanged, not the
   // 50/50/0 the caller expected. The fix draws a single Math.random()
   // per pixel and partitions the [0, pepper+salt) interval.
   test("salt and pepper probabilities are independent of order") {
      val source = ImmutableImage.filled(200, 200, Color.GRAY)
      val out = source.filter(SaltAndPepperFilter(0.5, 0.5))

      var black = 0
      var white = 0
      var gray = 0
      out.pixels().forEach { p ->
         val rgb = (p.red() shl 16) or (p.green() shl 8) or p.blue()
         when (rgb) {
            0x000000 -> black++
            0xFFFFFF -> white++
            else -> gray++
         }
      }

      // 40,000 pixels at 50/50 split → expect ~20,000 black, ~20,000 white,
      // ~0 gray. The pre-fix code produced ~20,000 black, ~10,000 white,
      // ~10,000 gray, with a ratio black:white ≈ 2:1. Tolerance is loose
      // (3 sigma ~= 150) but well inside the 10,000-pixel pre-fix gap.
      kotlin.math.abs(black - white) shouldBeLessThan 1000
      gray shouldBeLessThan 1000
   }
})
