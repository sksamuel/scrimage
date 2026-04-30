package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Pin the exact pixel-level math for OpacityFilter after the bulk-int
 * rewrite. The existing OpacityFilterTest in Scala compares against a
 * golden image; this is a smaller standalone test that catches drift
 * to the integer.
 */
class OpacityFilterValueTest : FunSpec({

   // Each channel: new = (int) (c + (255 - c) * amount)
   // amount = 0.5 → channel slides halfway toward white
   test("opacity 0.5 lifts each channel halfway toward 255") {
      val pixels = arrayOf(
         Pixel(0, 0, 0, 0, 0, 255),         // black → 127, 127, 127
         Pixel(1, 0, 100, 200, 50, 255),    // mid   → 177, 227, 152
         Pixel(0, 1, 255, 255, 255, 255),   // white stays white
         Pixel(1, 1, 0, 0, 0, 128)          // alpha is preserved verbatim
      )
      val image = ImmutableImage.create(2, 2, pixels)
      OpacityFilter(0.5f).apply(image)

      image.pixel(0, 0).let {
         it.red() shouldBe 127; it.green() shouldBe 127; it.blue() shouldBe 127
      }
      image.pixel(1, 0).let {
         it.red() shouldBe 177; it.green() shouldBe 227; it.blue() shouldBe 152
      }
      image.pixel(0, 1).let {
         it.red() shouldBe 255; it.green() shouldBe 255; it.blue() shouldBe 255
      }
      image.pixel(1, 1).alpha() shouldBe 128
   }

   test("opacity 0 leaves the image unchanged") {
      val pixels = arrayOf(Pixel(0, 0, 12, 34, 56, 200))
      val image = ImmutableImage.create(1, 1, pixels)
      OpacityFilter(0f).apply(image)
      image.pixel(0, 0).let {
         it.red() shouldBe 12; it.green() shouldBe 34; it.blue() shouldBe 56; it.alpha() shouldBe 200
      }
   }

   test("opacity 1 pushes every pixel to fully white (alpha intact)") {
      val pixels = arrayOf(Pixel(0, 0, 12, 34, 56, 200))
      val image = ImmutableImage.create(1, 1, pixels)
      OpacityFilter(1f).apply(image)
      image.pixel(0, 0).let {
         it.red() shouldBe 255; it.green() shouldBe 255; it.blue() shouldBe 255; it.alpha() shouldBe 200
      }
   }
})
