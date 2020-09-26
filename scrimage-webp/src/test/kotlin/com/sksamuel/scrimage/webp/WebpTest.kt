package com.sksamuel.scrimage.webp

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class WebpTest : FunSpec() {
   init {
      test("webp conversion") {
         val image = ImmutableImage.loader().fromResource("/test.webp")
         image.width shouldBe 2000
         image.height shouldBe 2000
      }
   }
}
