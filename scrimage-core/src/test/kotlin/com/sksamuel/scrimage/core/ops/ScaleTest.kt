@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.ops

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.ScaleMethod
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.shouldBe

class ScaleTest : FunSpec({

   val image = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/bird.jpg")

   test("Bicubic scale test") {
      image.scaleTo(486, 324) shouldBe ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/scale/bird_486_324_bicubic.png")
   }

   test("Bilinear scale test") {
      image.scaleTo(486, 324, ScaleMethod.Bilinear) shouldBe ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/scale/bird_486_324_bilinear.png")
   }

   test("Progressive bilinear scale down test") {
      image.scaleTo(486, 324, ScaleMethod.Progressive) shouldBe ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/scale/bird_486_324_prog_bilinear.png")
   }

   test("Progressive bilinear scale up test should complete") {
      image.scaleTo(3000, 3000, ScaleMethod.Progressive).width shouldBe 3000
      image.scaleTo(3000, 100, ScaleMethod.Progressive).height shouldBe 100
      image.scaleTo(100, 3000, ScaleMethod.Progressive).width shouldBe 100
   }

   test("when scaling an image the output image should match as expected") {
      val scaled = image.scale(0.25)
      val expected = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/bird_scale_025.png")
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
