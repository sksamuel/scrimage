package com.sksamuel.scrimage

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DropTest : FunSpec({

   val image = ImmutableImage.fromResource("/com/sksamuel/scrimage/bird.jpg").scaleTo(100, 80)

   test("dropLeft removes columns from the left edge and shrinks the width") {
      val out = image.dropLeft(30)
      out.width shouldBe 70
      out.height shouldBe 80
      // the new left column is the original column that was at x = 30
      out.pixel(0, 0).argb shouldBe image.pixel(30, 0).argb
      out.pixel(0, 40).argb shouldBe image.pixel(30, 40).argb
   }

   test("dropRight removes columns from the right edge and shrinks the width") {
      val out = image.dropRight(30)
      out.width shouldBe 70
      out.height shouldBe 80
      // the left side is unchanged
      out.pixel(0, 0).argb shouldBe image.pixel(0, 0).argb
      out.pixel(69, 10).argb shouldBe image.pixel(69, 10).argb
   }

   test("dropTop removes rows from the top edge and shrinks the height") {
      val out = image.dropTop(20)
      out.width shouldBe 100
      out.height shouldBe 60
      out.pixel(0, 0).argb shouldBe image.pixel(0, 20).argb
      out.pixel(50, 0).argb shouldBe image.pixel(50, 20).argb
   }

   test("dropBottom removes rows from the bottom edge and shrinks the height") {
      val out = image.dropBottom(20)
      out.width shouldBe 100
      out.height shouldBe 60
      out.pixel(0, 0).argb shouldBe image.pixel(0, 0).argb
      out.pixel(50, 59).argb shouldBe image.pixel(50, 59).argb
   }

   test("dropping more than the available pixels is clamped") {
      image.dropLeft(1000).width shouldBe 1
      image.dropTop(1000).height shouldBe 1
   }
})
