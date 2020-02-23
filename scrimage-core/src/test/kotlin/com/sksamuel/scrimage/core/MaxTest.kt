@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe

class MaxTest : FunSpec({

   val image = ImmutableImage.fromResource("/com/sksamuel/scrimage/bird.jpg")

   test("when maxing an image the dimensions should not exceed the bounds") {
      val maxed = image.max(20, 20)
      maxed.width.shouldBeLessThanOrEqual(20)
      maxed.height.shouldBeLessThanOrEqual(20)
   }

   test("when maxing an image vertically the height should equal the target height parameter") {
      val maxed = image.max(200, 20)
      20 shouldBe maxed.height
   }

   test("when maxing an image horizontally the width should equal the target width parameter") {
      val maxed = image.max(20, 156)
      20 shouldBe maxed.width
   }

   test("max operation happy path") {
      val maxed = image.max(200, 200)
      val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/bird_bound_200x200.png")
      maxed shouldBe expected
   }

})
