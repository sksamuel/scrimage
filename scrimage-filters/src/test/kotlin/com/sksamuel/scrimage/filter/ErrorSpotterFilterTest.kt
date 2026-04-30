package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.RGBColor
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Pin-down tests for the bulk-int rewrite of ErrorSpotterFilter. Each
 * channel diff is mirrored across two output channels (positive deltas
 * pile up red, negative deltas pile up blue), then both are clamped to
 * 255 after multiplying by ratio. Output alpha is always 255 (opaque).
 */
class ErrorSpotterFilterTest : FunSpec({

   test("identical images produce a fully black opaque diff") {
      val pixels = arrayOf(
         Pixel(0, 0, 100, 200, 50, 255),
         Pixel(1, 0, 0, 0, 0, 0),
         Pixel(0, 1, 255, 255, 255, 128),
         Pixel(1, 1, 50, 100, 150, 200)
      )
      val base = ImmutableImage.create(2, 2, pixels.copyOf())
      val src = ImmutableImage.create(2, 2, pixels.copyOf())
      ErrorSpotterFilter(base, 50).apply(src)
      // No deltas in any channel → red=0, blue=0, alpha=0xFF
      src.pixels().forEach { p ->
         p.red() shouldBe 0
         p.green() shouldBe 0
         p.blue() shouldBe 0
         p.alpha() shouldBe 255
      }
   }

   test("error() small positive red delta clamps via ratio") {
      // base red = 200, src red = 199 → delta = 1 → red += 1
      // ratio 50 → r = min(50 * 1, 255) = 50, b = 0
      val basePix = RGBColor(200, 100, 50, 255)
      val srcPix = RGBColor(199, 100, 50, 255)
      val base = ImmutableImage.create(1, 1, arrayOf(Pixel(0, 0, basePix.red, basePix.green, basePix.blue, basePix.alpha)))
      val filter = ErrorSpotterFilter(base, 50)
      val argb = filter.error(basePix, srcPix)
      ((argb ushr 24) and 0xFF) shouldBe 0xFF
      ((argb ushr 16) and 0xFF) shouldBe 50
      ((argb ushr 8) and 0xFF) shouldBe 0
      (argb and 0xFF) shouldBe 0
   }

   test("error() saturates at 255 when ratio*delta exceeds it") {
      // base red 200, src red 0 → delta 200 → r = min(50 * 200, 255) = 255
      val basePix = RGBColor(200, 100, 50, 255)
      val srcPix = RGBColor(0, 100, 50, 255)
      val base = ImmutableImage.create(1, 1, arrayOf(Pixel(0, 0, basePix.red, basePix.green, basePix.blue, basePix.alpha)))
      val filter = ErrorSpotterFilter(base, 50)
      val argb = filter.error(basePix, srcPix)
      ((argb ushr 16) and 0xFF) shouldBe 255 // red saturated
      (argb and 0xFF) shouldBe 0
   }

   test("error() routes negative deltas into blue channel") {
      // base red 0, src red 100 → delta -100 → blue += 100 → b = min(50*100, 255) = 255
      val basePix = RGBColor(0, 0, 0, 255)
      val srcPix = RGBColor(100, 0, 0, 255)
      val base = ImmutableImage.create(1, 1, arrayOf(Pixel(0, 0, basePix.red, basePix.green, basePix.blue, basePix.alpha)))
      val filter = ErrorSpotterFilter(base, 50)
      val argb = filter.error(basePix, srcPix)
      ((argb ushr 16) and 0xFF) shouldBe 0
      (argb and 0xFF) shouldBe 255 // blue saturated
   }

   test("apply() over an image preserves dimensions and writes opaque output") {
      val basePixels = arrayOf(
         Pixel(0, 0, 100, 100, 100, 255),
         Pixel(1, 0, 100, 100, 100, 255)
      )
      val srcPixels = arrayOf(
         Pixel(0, 0, 110, 100, 100, 255),  // +10 red
         Pixel(1, 0, 90, 100, 100, 255)    // -10 red
      )
      val base = ImmutableImage.create(2, 1, basePixels)
      val src = ImmutableImage.create(2, 1, srcPixels)
      ErrorSpotterFilter(base, 5).apply(src)
      src.width shouldBe 2
      src.height shouldBe 1
      // Output is always opaque
      src.pixels().forEach { it.alpha() shouldBe 255 }
      // First pixel: src red is HIGHER than base → that's a negative delta from
      // base→src, so error() puts it in BLUE channel (5*10=50)
      src.pixel(0, 0).blue() shouldBe 50
      src.pixel(0, 0).red() shouldBe 0
      // Second pixel: src red is LOWER than base → positive delta → RED (5*10=50)
      src.pixel(1, 0).red() shouldBe 50
      src.pixel(1, 0).blue() shouldBe 0
   }
})
