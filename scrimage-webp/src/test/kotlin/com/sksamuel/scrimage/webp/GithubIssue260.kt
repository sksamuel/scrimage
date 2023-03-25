package com.sksamuel.scrimage.webp

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class GithubIssue260 : FunSpec() {
   init {
      test("webp fails to parse") {
         val image = ImmutableImage.loader().fromResource("/github260.webp")
         image.width shouldBe 900
      }
   }
}
