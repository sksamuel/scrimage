package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RepeaterFilterTest : FunSpec({

   test("RepeaterFilter scales the canvas by the grid dimensions") {
      val image = ImmutableImage.create(120, 90)
      val out = RepeaterFilter(2, 3).apply(image)
      out.width shouldBe 240
      out.height shouldBe 270
   }

   test("RepeaterFilter(1, 1) leaves the image unchanged") {
      val image = ImmutableImage.create(40, 40).fill(java.awt.Color(10, 20, 30))
      val out = RepeaterFilter(1, 1).apply(image)
      out shouldBe image
   }

   test("RepeaterFilter tiles the source across rows and columns at full size") {
      // a uniform 2x2 source tiled into a 3x2 grid should produce a 6x4 image
      // whose every pixel matches the source colour (including the far corner).
      val image = ImmutableImage.create(2, 2).fill(java.awt.Color(200, 100, 50))
      val out = RepeaterFilter(3, 2).apply(image)
      out.width shouldBe 6
      out.height shouldBe 4
      out.pixel(5, 3).toInt() shouldBe image.pixel(0, 0).toInt()
   }

   test("RepeaterFilter rejects non-positive grid dimensions") {
      shouldThrow<IllegalArgumentException> { RepeaterFilter(0, 2) }
      shouldThrow<IllegalArgumentException> { RepeaterFilter(2, 0) }
   }
})
