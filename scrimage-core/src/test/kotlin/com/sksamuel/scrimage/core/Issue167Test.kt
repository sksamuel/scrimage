package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class Issue167Test : FunSpec({
   test("Jpeg orientation handling problem #167") {
      val image = ImmutableImage.fromResource("/issue167.jpg")
      image.width shouldBe 2240
      image.height shouldBe 3984
   }
})