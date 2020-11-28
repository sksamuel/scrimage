package com.sksamuel.scrimage.transform

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BackgroundGradientTest : FunSpec({

   test("bird") {
      val image = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/bird.jpg")
      val result = image.scaleTo(300, 200).transform(BackgroundGradient(400, 300))
      result shouldBe ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/transform/bird_bg.png")
   }

   test("jazz") {
      val image = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/jazz.jpg")
      val result = image.scaleTo(300, 200).transform(BackgroundGradient(400, 300))
      result shouldBe ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/transform/jazz_bg.png")
   }

   test("enterprise-d") {
      val image = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/enterprised.jpg")
      val result = image.scaleTo(300, 200).transform(BackgroundGradient(400, 300))
      result shouldBe ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/transform/enterprised_bg.png")
   }

   test("star_trek_2") {
      val image = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/star_trek_2.jpg")
      val result = image.scaleTo(300, 200).transform(BackgroundGradient(400, 300))
      result shouldBe ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/transform/star_trek_2_bg.png")
   }

   test("star_wars_9") {
      val image = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/star_wars_9.jpeg")
      val result = image.scaleTo(300, 200).transform(BackgroundGradient(400, 300))
      result shouldBe ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/transform/star_wars_9_bg.png")
   }
})
