package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import java.awt.image.BufferedImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class AwtImageTest : FunSpec({

   // Regression tests for https://github.com/sksamuel/scrimage/pull/351
   // k was never incremented, so every patch overwrote patches[0]

   test("patches returns the correct number of non-null patches") {
      val image = ImmutableImage.create(4, 4)
      val patches = image.patches(2, 2)
      patches.size shouldBe (4 - 2) * (4 - 2)  // = 4
      patches.forEach { it shouldNotBe null }
   }

   test("patches samples pixels from the correct region") {
      // 4x4 image where each pixel's red channel equals its flat index (0..15)
      val pixels = Array(16) { i -> Pixel(i % 4, i / 4, i, 0, 0, 255) }
      val image = ImmutableImage.create(4, 4, pixels)

      // 1x1 patches: (4-1)*(4-1) = 9; each patch contains one pixel
      val patches = image.patches(1, 1)
      patches.size shouldBe 9

      // patch 0 → col=0, row=0 → pixel index 0 → red=0
      patches[0][0].red() shouldBe 0
      // patch 1 → col=1, row=0 → pixel index 1 → red=1
      patches[1][0].red() shouldBe 1
      // patch 3 → col=0, row=1 → pixel index 4 → red=4
      patches[3][0].red() shouldBe 4
   }

   test("AwtImage.points return array of points") {
      val image = ImmutableImage.create(6, 4)
      image.points().toList() shouldBe listOf(
         java.awt.Point(0, 0),
         java.awt.Point(1, 0),
         java.awt.Point(2, 0),
         java.awt.Point(3, 0),
         java.awt.Point(4, 0),
         java.awt.Point(5, 0),
         java.awt.Point(0, 1),
         java.awt.Point(1, 1),
         java.awt.Point(2, 1),
         java.awt.Point(3, 1),
         java.awt.Point(4, 1),
         java.awt.Point(5, 1),
         java.awt.Point(0, 2),
         java.awt.Point(1, 2),
         java.awt.Point(2, 2),
         java.awt.Point(3, 2),
         java.awt.Point(4, 2),
         java.awt.Point(5, 2),
         java.awt.Point(0, 3),
         java.awt.Point(1, 3),
         java.awt.Point(2, 3),
         java.awt.Point(3, 3),
         java.awt.Point(4, 3),
         java.awt.Point(5, 3)
      )
   }

   test("pixels() returns correct non-null array for TYPE_4BYTE_ABGR image") {
      // A BufferedImage with TYPE_4BYTE_ABGR uses a DataBufferByte, so pixels()
      // falls through to the getRGB per-pixel path. This test locks in that
      // contract: correct length, no nulls, coordinates in row-major order,
      // and ARGB values preserved.
      val bi = BufferedImage(2, 2, BufferedImage.TYPE_4BYTE_ABGR)
      bi.setRGB(0, 0, 0xFFFF0000.toInt()) // opaque red
      bi.setRGB(1, 0, 0xFF00FF00.toInt()) // opaque green
      bi.setRGB(0, 1, 0xFF0000FF.toInt()) // opaque blue
      bi.setRGB(1, 1, 0xFFFFFFFF.toInt()) // opaque white

      val image = ImmutableImage.wrapAwt(bi)
      val pixels = image.pixels()

      pixels.size shouldBe 4
      pixels.forEach { it shouldNotBe null }

      pixels[0].x shouldBe 0; pixels[0].y shouldBe 0
      pixels[0].argb shouldBe 0xFFFF0000.toInt()

      pixels[1].x shouldBe 1; pixels[1].y shouldBe 0
      pixels[1].argb shouldBe 0xFF00FF00.toInt()

      pixels[2].x shouldBe 0; pixels[2].y shouldBe 1
      pixels[2].argb shouldBe 0xFF0000FF.toInt()

      pixels[3].x shouldBe 1; pixels[3].y shouldBe 1
      pixels[3].argb shouldBe 0xFFFFFFFF.toInt()
   }
})
