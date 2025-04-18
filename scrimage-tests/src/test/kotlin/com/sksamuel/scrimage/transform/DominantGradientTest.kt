package com.sksamuel.scrimage.transform

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PngWriter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DominantGradientTest : FunSpec({

   test("bird") {
      val image = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/bird.jpg")
      val result = image.scaleTo(300, 200).transform(DominantGradient())
      result.forWriter(PngWriter()).write("bird.png")
      result shouldBe ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/transform/dominant/bird.png")
   }

   test("jazz") {
      val image = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/jazz.jpg")
      val result = image.scaleTo(300, 200).transform(DominantGradient())
      result.forWriter(PngWriter()).write("jazz.png")
      result shouldBe ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/transform/dominant/jazz.png")
   }

   test("enterprise-d") {
      val image = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/enterprised.jpg")
      val result = image.scaleTo(300, 200).transform(DominantGradient())
      result.forWriter(PngWriter()).write("enterprised.png")
      result shouldBe ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/transform/dominant/enterprised.png")
   }

   test("star_trek_2") {
      val image = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/star_trek_2.jpg")
      val result = image.scaleTo(300, 200).transform(DominantGradient())
      result.forWriter(PngWriter()).write("star_trek_2.png")
      result shouldBe ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/transform/dominant/star_trek_2.png")
   }

   test("star_wars_9") {
      val image = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/star_wars_9.jpeg")
      val result = image.scaleTo(300, 200).transform(DominantGradient())
      result.forWriter(PngWriter()).write("star_wars_9.png")
      result shouldBe ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/transform/dominant/star_wars_9.png")
   }
})
