package com.sksamuel.scrimage.core.color

import com.sksamuel.scrimage.color.RGBColor
import com.sksamuel.scrimage.pixels.Pixel
import com.sksamuel.scrimage.pixels.PixelTools
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Regression tests for the average() grayscale calculation.
 *
 * RGBColor.average(), Pixel.average(), Pixel.toAverageGrayscale() and
 * PixelTools.gray() previously truncated the arithmetic mean using integer
 * division ((r + g + b) / 3), while the sibling grayscale methods
 * LumaGrayscale and WeightedGrayscale both round via Math.round.
 *
 * These tests pin the rounded behaviour, e.g. RGB(0, 0, 2) averages to
 * round(2/3) = 1, not 0.
 */
class AverageRoundingTest : FunSpec({

   test("RGBColor.average rounds rather than truncates") {
      RGBColor(0, 0, 2).average() shouldBe 1   // 2/3  = 0.67 -> 1
      RGBColor(0, 0, 5).average() shouldBe 2   // 5/3  = 1.67 -> 2
      RGBColor(0, 0, 1).average() shouldBe 0   // 1/3  = 0.33 -> 0
      RGBColor(1, 1, 2).average() shouldBe 1   // 4/3  = 1.33 -> 1
      RGBColor(255, 255, 254).average() shouldBe 255 // 764/3 = 254.67 -> 255
   }

   test("RGBColor.average is exact when the sum divides evenly") {
      RGBColor(0, 0, 0).average() shouldBe 0
      RGBColor(100, 150, 200).average() shouldBe 150
      RGBColor(255, 255, 255).average() shouldBe 255
   }

   test("Pixel.average rounds rather than truncates") {
      Pixel(0, 0, 0, 0, 2, 255).average() shouldBe 1
      Pixel(0, 0, 0, 0, 5, 255).average() shouldBe 2
      Pixel(0, 0, 0, 0, 1, 255).average() shouldBe 0
      Pixel(0, 0, 255, 255, 254, 255).average() shouldBe 255
   }

   test("Pixel.toAverageGrayscale rounds rather than truncates") {
      val gray = Pixel(0, 0, 0, 0, 2, 255).toAverageGrayscale()
      gray.red() shouldBe 1
      gray.green() shouldBe 1
      gray.blue() shouldBe 1

      val gray2 = Pixel(0, 0, 0, 0, 5, 128).toAverageGrayscale()
      gray2.red() shouldBe 2
      gray2.green() shouldBe 2
      gray2.blue() shouldBe 2
      gray2.alpha() shouldBe 128
   }

   test("PixelTools.gray rounds rather than truncates") {
      PixelTools.gray(PixelTools.argb(255, 0, 0, 2)) shouldBe 1
      PixelTools.gray(PixelTools.argb(255, 0, 0, 5)) shouldBe 2
      PixelTools.gray(PixelTools.argb(255, 100, 150, 200)) shouldBe 150
   }

   test("RGBColor.average and Pixel.average agree for the same channels") {
      val channels = listOf(
         Triple(0, 0, 2),
         Triple(0, 0, 5),
         Triple(1, 2, 3),
         Triple(13, 77, 200),
         Triple(254, 255, 255),
      )
      channels.forEach { (r, g, b) ->
         RGBColor(r, g, b).average() shouldBe Pixel(0, 0, r, g, b, 255).average()
      }
   }
})
