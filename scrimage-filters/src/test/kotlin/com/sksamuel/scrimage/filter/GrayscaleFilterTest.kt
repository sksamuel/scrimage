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
})
