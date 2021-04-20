package com.sksamuel.scrimage.core.ops

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CoverTest : FunSpec({

   val image = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/bird.jpg")

   test("when covering an image the output image should have the specified dimensions") {
      val covered = image.cover(51, 66)
      51 shouldBe covered.width
      66 shouldBe covered.height
   }

   test("cover operation happy path") {
      val covered = image.cover(200, 200)
      val expected = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/bird_cover_200x200.png")
      covered shouldBe expected
   }

})
