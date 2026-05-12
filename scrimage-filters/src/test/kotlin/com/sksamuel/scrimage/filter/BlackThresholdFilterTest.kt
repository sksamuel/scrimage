@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BlackThresholdFilterTest : FunSpec({

   test("black threshold filter output matches expected") {

      val image = ImmutableImage.fromStream(javaClass.getResourceAsStream("/bird_small.png"))
      val thresholdPercentage = 40.0

      BlackThresholdFilter(thresholdPercentage).apply(image)

      val expected =
         ImmutableImage.fromStream(javaClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_black_threshold.png"))
      image shouldBe expected
   }

   // Pin pixel-level behaviour after the bulk-int rewrite. brightness =
   // (r + g + b) / 3; pixels strictly below the threshold get RGB cleared
   // to zero while the alpha channel is preserved verbatim.
   test("pixels below threshold are blacked out, pixels at-or-above are kept, alpha preserved") {
      val threshold = 50.0 // 50% of 255 = 127
      val pixels = arrayOf(
         // brightness 50 (below) — should be blacked, alpha=200 kept
         Pixel(0, 0, 50, 50, 50, 200),
         // brightness 200 (above) — should be kept verbatim, alpha=128
         Pixel(1, 0, 200, 200, 200, 128),
         // brightness exactly 127 (boundary, NOT below) — kept
         Pixel(0, 1, 127, 127, 127, 255),
         // brightness 0 (below) — blacked, alpha=0 (transparent black stays transparent)
         Pixel(1, 1, 0, 0, 0, 0)
      )
      val image = ImmutableImage.create(2, 2, pixels)
      BlackThresholdFilter(threshold).apply(image)

      image.pixel(0, 0).let {
         it.red() shouldBe 0; it.green() shouldBe 0; it.blue() shouldBe 0; it.alpha() shouldBe 200
      }
      image.pixel(1, 0).let {
         it.red() shouldBe 200; it.green() shouldBe 200; it.blue() shouldBe 200; it.alpha() shouldBe 128
      }
      image.pixel(0, 1).let {
         it.red() shouldBe 127; it.green() shouldBe 127; it.blue() shouldBe 127; it.alpha() shouldBe 255
      }
      image.pixel(1, 1).let {
         it.red() shouldBe 0; it.green() shouldBe 0; it.blue() shouldBe 0; it.alpha() shouldBe 0
      }
   }

})
