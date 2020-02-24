@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.ops

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs

class FlipTest : FunSpec({

   val image = ImmutableImage.fromResource("/com/sksamuel/scrimage/bird.jpg")
   val turing = ImmutableImage.fromResource("/com/sksamuel/scrimage/turing.jpg")

   test("when flipping on x axis the dimensions are retained") {
      val flipped = image.flipX()
      1944 shouldBe flipped.width
      1296 shouldBe flipped.height
   }

   test("when flipping on x axis the image is flipped horizontally") {
      turing.flipX() shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/turing_flipx.png")
   }

   test("when flipping on y axis the image is flipped vertically") {
      turing.flipY() shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/turing_flipy.png")
   }

   test("when flipping on y axis the dimensions are retained") {
      val flipped = image.flipY()
      1944 shouldBe flipped.width
      1296 shouldBe flipped.height
   }

   test("when flipping on x axis a new image is created") {
      val flipped = image.flipX()
      flipped shouldNotBeSameInstanceAs image
   }

   test("when flipping on y axis a new image is created") {
      val flipped = image.flipY()
      flipped shouldNotBeSameInstanceAs image
   }

})
