package com.sksamuel.scrimage.transform

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DominantGradientTest : FunSpec({

   test("bird") {
      val image = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/bird.jpg")
      val result = image.scaleTo(300, 200).transform(DominantGradient())
      result shouldBe ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/transform/dominant/bird.png")
   }

   test("jazz") {
      val image = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/jazz.jpg")
      val result = image.scaleTo(300, 200).transform(DominantGradient())
      result shouldBe ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/transform/dominant/jazz.png")
   }

   test("enterprise-d") {
      val image = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/enterprised.jpg")
      val result = image.scaleTo(300, 200).transform(DominantGradient())
      result shouldBe ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/transform/dominant/enterprised.png")
   }

   test("star_trek_2") {
      val image = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/star_trek_2.jpg")
      val result = image.scaleTo(300, 200).transform(DominantGradient())
      result shouldBe ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/transform/dominant/star_trek_2.png")
   }

   test("star_wars_9") {
      val image = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/star_wars_9.jpeg")
      val result = image.scaleTo(300, 200).transform(DominantGradient())
      result shouldBe ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/transform/dominant/star_wars_9.png")
   }
})
