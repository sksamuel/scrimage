package com.sksamuel.scrimage.core.subpixel

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class LinearSubpixelInterpolatorTest : FunSpec({

   val image = ImmutableImage.fromResource("/com/sksamuel/scrimage/bird.jpg")

   test("subpixel happy path") {
      -1315602 shouldBe image.subpixel(2.0, 3.0)
      -1381395 shouldBe image.subpixel(22.0, 63.0)
      -2038553 shouldBe image.subpixel(152.0, 383.0)
   }

   test("subimage has right dimensions") {
      val covered = image.cover(200, 200)
      val subimage = covered.subimage(10, 10, 50, 100)
      50 shouldBe subimage.width
      100 shouldBe subimage.height
   }

   // The interpolator picks 1, 2, or 4 neighbours depending on whether a
   // coordinate is within half a pixel of the edge. The next three tests
   // exercise each of those code paths against a small constructed image
   // with known pixel values.
   test("subpixel near top-left corner uses only the (0,0) pixel") {
      val pixels = arrayOf(
         Pixel(0, 0, 0xFFFF0000.toInt()), // opaque red
         Pixel(1, 0, 0xFF00FF00.toInt()), // opaque green
         Pixel(0, 1, 0xFF0000FF.toInt()), // opaque blue
         Pixel(1, 1, 0xFFFFFFFF.toInt())  // opaque white
      )
      val img = ImmutableImage.create(2, 2, pixels)
      // x <= 0.5 and y <= 0.5 → both axes return the single (0, weight=1) pair
      img.subpixel(0.25, 0.25) shouldBe 0xFFFF0000.toInt()
   }

   test("subpixel exactly between two columns averages 2 pixels per row") {
      val pixels = arrayOf(
         // row 0: red, green
         Pixel(0, 0, 0xFFFF0000.toInt()),
         Pixel(1, 0, 0xFF00FF00.toInt()),
         // row 1: red, green
         Pixel(0, 1, 0xFFFF0000.toInt()),
         Pixel(1, 1, 0xFF00FF00.toInt())
      )
      val img = ImmutableImage.create(2, 2, pixels)
      // y = 0.25 → only top row; x = 1.0 → average of (0,0) and (1,0) with 0.5 each
      // expected = 0.5*(0xFF, 0xFF, 0, 0) + 0.5*(0xFF, 0, 0xFF, 0) = (0xFF, 128, 128, 0)
      val argb = img.subpixel(1.0, 0.25)
      ((argb ushr 24) and 0xFF) shouldBe 0xFF
      ((argb ushr 16) and 0xFF) shouldBe 128
      ((argb ushr 8) and 0xFF) shouldBe 128
      (argb and 0xFF) shouldBe 0
   }

   test("subpixel in the middle blends all 4 neighbours equally") {
      val pixels = arrayOf(
         Pixel(0, 0, 0xFFFF0000.toInt()), // red
         Pixel(1, 0, 0xFF00FF00.toInt()), // green
         Pixel(0, 1, 0xFF0000FF.toInt()), // blue
         Pixel(1, 1, 0xFF000000.toInt())  // black (opaque)
      )
      val img = ImmutableImage.create(2, 2, pixels)
      // x=1.0, y=1.0 → 4 neighbours each with weight 0.25
      // r = 0.25*(255+0+0+0) = 63.75 → 64
      // g = 0.25*(0+255+0+0) = 63.75 → 64
      // b = 0.25*(0+0+255+0) = 63.75 → 64
      // a = 0.25*(255+255+255+255) = 255
      val argb = img.subpixel(1.0, 1.0)
      ((argb ushr 24) and 0xFF) shouldBe 255
      ((argb ushr 16) and 0xFF) shouldBe 64
      ((argb ushr 8) and 0xFF) shouldBe 64
      (argb and 0xFF) shouldBe 64
   }

   // The int[]-direct rewrite of subpixelSubimage must produce the same
   // pixel grid as the Pixel[]-roundtrip implementation. Pin it to the
   // existing happy-path subpixel values for an extracted region.
   test("subpixelSubimage at integer offsets returns the expected pixels") {
      // Same image and same coordinates as 'subpixel happy path', so the
      // output of the (1, 1) pixel of subpixelSubimage(2.0, 3.0, 3, 3)
      // should equal subpixel(3.0, 4.0).
      val sub = image.subpixelSubimage(2.0, 3.0, 3, 3)
      sub.width shouldBe 3
      sub.height shouldBe 3
      sub.pixel(1, 1).argb shouldBe image.subpixel(3.0, 4.0)
      sub.pixel(0, 0).argb shouldBe image.subpixel(2.0, 3.0)
      sub.pixel(2, 2).argb shouldBe image.subpixel(4.0, 5.0)
   }
})
