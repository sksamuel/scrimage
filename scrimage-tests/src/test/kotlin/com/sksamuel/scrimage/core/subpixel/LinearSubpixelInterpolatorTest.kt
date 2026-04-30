package com.sksamuel.scrimage.core.subpixel

import com.sksamuel.scrimage.ImmutableImage
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
