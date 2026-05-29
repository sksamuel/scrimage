package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MirrorFilterTest : FunSpec({

   test("MirrorFilter flips the image left to right") {
      // 3x2 image where each pixel's red channel encodes its column (0,1,2)
      val pixels = Array(6) { i -> Pixel(i % 3, i / 3, (i % 3) * 80, 0, 0, 255) }
      val image = ImmutableImage.create(3, 2, pixels)

      val mirrored = image.filter(MirrorFilter())

      mirrored.width shouldBe 3
      mirrored.height shouldBe 2
      // each row's columns are reversed: col0<->col2, col1 unchanged
      for (y in 0 until 2) {
         mirrored.pixel(0, y).red() shouldBe 160
         mirrored.pixel(1, y).red() shouldBe 80
         mirrored.pixel(2, y).red() shouldBe 0
      }
   }

   test("MirrorFilter applied twice returns the original") {
      val pixels = Array(20) { i -> Pixel(i % 5, i / 5, i * 10, i, 0, 255) }
      val image = ImmutableImage.create(5, 4, pixels)
      val twice = image.filter(MirrorFilter()).filter(MirrorFilter())
      twice shouldBe image
   }
})
