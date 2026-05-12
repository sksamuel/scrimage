package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

class GrayscaleFilterTest : FunSpec({

   // Regression: GrayscaleFilter used luma weights 0.21/0.71/0.07 which sum to 0.99,
   // not 1.0. A pure white pixel (255,255,255) mapped to gray 252 instead of 255.
   // Fix: use the correct Rec. 709 weights 0.2126/0.7152/0.0722 (sum = 1.0).
   test("white pixel maps to white after grayscale") {
      val pixels = arrayOf(Pixel(0, 0, 255, 255, 255, 255))
      val image = ImmutableImage.create(1, 1, pixels)
      image.filter(GrayscaleFilter())
      val result = image.filter(GrayscaleFilter())
      val p = result.pixel(0, 0)
      p.red() shouldBe 255
      p.green() shouldBe 255
      p.blue() shouldBe 255
   }

   test("black pixel maps to black after grayscale") {
      val pixels = arrayOf(Pixel(0, 0, 0, 0, 0, 255))
      val image = ImmutableImage.create(1, 1, pixels)
      val result = image.filter(GrayscaleFilter())
      val p = result.pixel(0, 0)
      p.red() shouldBe 0
      p.green() shouldBe 0
      p.blue() shouldBe 0
   }

   test("grayscale preserves alpha") {
      val pixels = arrayOf(Pixel(0, 0, 200, 100, 50, 128))
      val image = ImmutableImage.create(1, 1, pixels)
      val result = image.filter(GrayscaleFilter())
      result.pixel(0, 0).alpha() shouldBe 128
   }

   test("grayscale output is neutral gray (R == G == B)") {
      val pixels = arrayOf(Pixel(0, 0, Color(100, 150, 200).rgb).let {
         Pixel(0, 0, 100, 150, 200, 255)
      })
      val image = ImmutableImage.create(1, 1, pixels)
      val result = image.filter(GrayscaleFilter())
      val p = result.pixel(0, 0)
      p.red() shouldBe p.green()
      p.green() shouldBe p.blue()
   }

   // The bulk-int rewrite must compute the same luma as the previous
   // mapInPlace((Pixel) -> Color) implementation. Pin the exact value
   // for a known input.
   test("grayscale luma matches Rec. 709 weights to the integer") {
      // gray = round(0.2126*100 + 0.7152*150 + 0.0722*200)
      //      = round(21.26 + 107.28 + 14.44)
      //      = round(142.98) = 143
      val pixels = arrayOf(Pixel(0, 0, 100, 150, 200, 255))
      val image = ImmutableImage.create(1, 1, pixels)
      val result = image.filter(GrayscaleFilter())
      val p = result.pixel(0, 0)
      p.red() shouldBe 143
      p.green() shouldBe 143
      p.blue() shouldBe 143
      p.alpha() shouldBe 255
   }

   test("grayscale leaves a multi-pixel image with row-major ordering intact") {
      val pixels = arrayOf(
         Pixel(0, 0, 255, 0, 0, 255),    // red    → gray ≈ 54
         Pixel(1, 0, 0, 255, 0, 255),    // green  → gray ≈ 182
         Pixel(0, 1, 0, 0, 255, 255),    // blue   → gray ≈ 18
         Pixel(1, 1, 255, 255, 255, 255) // white  → gray = 255
      )
      val image = ImmutableImage.create(2, 2, pixels)
      val result = image.filter(GrayscaleFilter())
      result.pixel(0, 0).red() shouldBe 54
      result.pixel(1, 0).red() shouldBe 182
      result.pixel(0, 1).red() shouldBe 18
      result.pixel(1, 1).red() shouldBe 255
   }
})
