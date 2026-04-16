package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
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
})
