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

})
