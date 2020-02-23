package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BoundTest : FunSpec({

   val image = ImmutableImage.fromResource("/com/sksamuel/scrimage/bird.jpg")

   test("when bounding, an image under the bounds should not change") {
      val bounded = image.bound(10000, 10000)
      bounded shouldBe image
   }

   test("when bounding, an image on the bounds should not change") {
      image.bound(image.width, image.height) shouldBe image
      image.bound(image.width * 2, image.height) shouldBe image
      image.bound(image.width, image.height * 2) shouldBe image
   }

   test("when bounding, an image larger than the bounds should be resized") {
      image.bound(image.width / 2, image.height).width shouldBe image.width / 2
      image.bound(image.width / 2, image.height).height shouldBe image.height / 2
      image.bound(image.width, image.height / 2).width shouldBe image.width / 2
      image.bound(image.width, image.height / 2).height shouldBe image.height / 2
   }

})
