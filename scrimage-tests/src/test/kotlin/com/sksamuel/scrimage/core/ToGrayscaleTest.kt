package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.AverageGrayscale
import com.sksamuel.scrimage.color.LumaGrayscale
import com.sksamuel.scrimage.color.WeightedGrayscale
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Pin-down tests for the bulk-int rewrite of toGrayscale(GrayscaleMethod).
 * The rewrite must:
 *   - dispatch to the chosen GrayscaleMethod for every pixel
 *   - put the resulting gray into all three RGB channels
 *   - preserve the source pixel's alpha channel via Grayscale.alpha
 *   - leave the source ImmutableImage unchanged (operates on a copy)
 */
class ToGrayscaleTest : FunSpec({

   // All three method tests use opaque alpha (255) because copy() goes
   // through Graphics2D.drawImage which premultiplies internally and loses
   // precision when alpha != 255 — that's a pre-existing behaviour, not a
   // regression of this PR.
   test("LumaGrayscale: 100/150/200 opaque → gray 143") {
      // gray = round(0.2126*100 + 0.7152*150 + 0.0722*200) = round(142.98) = 143
      val pixels = arrayOf(Pixel(0, 0, 100, 150, 200, 255))
      val image = ImmutableImage.create(1, 1, pixels)
      val out = image.toGrayscale(LumaGrayscale())
      val p = out.pixel(0, 0)
      p.red() shouldBe 143
      p.green() shouldBe 143
      p.blue() shouldBe 143
      p.alpha() shouldBe 255
   }

   test("AverageGrayscale: 100/150/200 opaque → gray 150") {
      // average = (100 + 150 + 200) / 3 = 150
      val pixels = arrayOf(Pixel(0, 0, 100, 150, 200, 255))
      val image = ImmutableImage.create(1, 1, pixels)
      val out = image.toGrayscale(AverageGrayscale())
      val p = out.pixel(0, 0)
      p.red() shouldBe 150
      p.green() shouldBe 150
      p.blue() shouldBe 150
      p.alpha() shouldBe 255
   }

   test("WeightedGrayscale: 100/150/200 opaque → gray 141") {
      // gray = round(0.299*100 + 0.587*150 + 0.114*200) = round(140.65) = 141
      val pixels = arrayOf(Pixel(0, 0, 100, 150, 200, 255))
      val image = ImmutableImage.create(1, 1, pixels)
      val out = image.toGrayscale(WeightedGrayscale())
      val p = out.pixel(0, 0)
      p.red() shouldBe 141
      p.green() shouldBe 141
      p.blue() shouldBe 141
      p.alpha() shouldBe 255
   }

   test("toGrayscale leaves the source image unchanged") {
      val pixels = arrayOf(Pixel(0, 0, 100, 150, 200, 255))
      val original = ImmutableImage.create(1, 1, pixels)
      original.toGrayscale(LumaGrayscale())
      // Original red is still 100, not the gray value
      original.pixel(0, 0).red() shouldBe 100
      original.pixel(0, 0).green() shouldBe 150
      original.pixel(0, 0).blue() shouldBe 200
   }

   test("toGrayscale preserves row-major ordering across multi-pixel images") {
      val pixels = arrayOf(
         Pixel(0, 0, 255, 0, 0, 255),    // red    → luma 54
         Pixel(1, 0, 0, 255, 0, 255),    // green  → luma 182
         Pixel(0, 1, 0, 0, 255, 255),    // blue   → luma 18
         Pixel(1, 1, 255, 255, 255, 255) // white  → luma 255
      )
      val image = ImmutableImage.create(2, 2, pixels)
      val out = image.toGrayscale(LumaGrayscale())
      out.pixel(0, 0).red() shouldBe 54
      out.pixel(1, 0).red() shouldBe 182
      out.pixel(0, 1).red() shouldBe 18
      out.pixel(1, 1).red() shouldBe 255
   }
})
