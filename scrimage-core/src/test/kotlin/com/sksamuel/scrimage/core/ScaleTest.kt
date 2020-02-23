@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.shouldBe

class ScaleTest : FunSpec({

   val image = ImmutableImage.fromResource("/com/sksamuel/scrimage/bird.jpg")

   test("scaling correct scales the image") {
      image.scaleTo(486, 324) shouldBe ImmutableImage.fromResource("/bird_486_324.png")
   }

   test("when scaling an image the output image should match as expected") {
      val scaled = image.scale(0.25)
      val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/bird_scale_025.png")
      expected.width shouldBe scaled.width
      expected.height shouldBe scaled.height
   }

   test("when scaling an image the output image should have specified dimensions") {
      val scaled = image.scaleTo(900, 300)
      scaled.width shouldBe 900
      scaled.height shouldBe 300
   }

   test("when scaling by width then target image maintains aspect ratio") {
      val scaled = image.scaleToWidth(500)
      scaled.width shouldBe 500
      scaled.ratio() - image.ratio() shouldBeLessThan 0.01
   }

   test("when scaling by height then target image maintains aspect ratio") {
      val scaled = image.scaleToHeight(400)
      scaled.height shouldBe 400
      scaled.ratio() - image.ratio() shouldBeLessThan 0.01
   }

})
