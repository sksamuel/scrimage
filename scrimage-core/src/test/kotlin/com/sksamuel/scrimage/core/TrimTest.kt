package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TrimTest : FunSpec({

   val image = ImmutableImage.fromResource("/com/sksamuel/scrimage/bird.jpg")

   test("when trimming the image has the trimmed dimensions") {
      val trimmed = image.trim(3, 4, 5, 6)
      image.width - 3 - 5 shouldBe trimmed.width
      image.height - 4 - 6 shouldBe trimmed.height
   }

   test("when trimming the image is not empty") {
      val trimmed = image.trim(3, 4, 5, 6)
      trimmed.forAll { p -> p.toARGBInt().toLong() == 0xFF000000 || p.toARGBInt().toLong() == 0xFFFFFFFF } shouldBe false
   }

   test("trim should revert padWith") {
      val image = ImmutableImage.create(85, 56)
      val same = image.padWith(10, 2, 5, 7).trim(10, 2, 5, 7)
      image.width shouldBe same.width
      image.height shouldBe same.height
   }
})
