package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RepeaterFilterTest : FunSpec({

   test("RepeaterFilter enlarges the image to columns x rows of the source") {
      val image = ImmutableImage.create(40, 30)
      val out = image.transform(RepeaterFilter(2, 3))
      out.width shouldBe 80
      out.height shouldBe 90
   }

   test("each tile is a copy of the source image") {
      // 2x2 image with a distinct colour per pixel
      val pixels = arrayOf(
         Pixel(0, 0, 255, 0, 0, 255),
         Pixel(1, 0, 0, 255, 0, 255),
         Pixel(0, 1, 0, 0, 255, 255),
         Pixel(1, 1, 255, 255, 0, 255)
      )
      val image = ImmutableImage.create(2, 2, pixels)
      val out = image.transform(RepeaterFilter(2, 3))
      out.width shouldBe 4
      out.height shouldBe 6
      // top-left pixel of every tile equals the source top-left (red)
      for (c in 0 until 2) {
         for (r in 0 until 3) {
            out.pixel(c * 2, r * 2).argb shouldBe image.pixel(0, 0).argb
            out.pixel(c * 2 + 1, r * 2 + 1).argb shouldBe image.pixel(1, 1).argb
         }
      }
   }

   test("RepeaterFilter rejects non-positive grid dimensions") {
      shouldThrow<IllegalArgumentException> { RepeaterFilter(0, 2) }
      shouldThrow<IllegalArgumentException> { RepeaterFilter(2, 0) }
   }
})
