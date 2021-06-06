package com.sksamuel.scrimage.core.autocrop

import com.sksamuel.scrimage.Dimension
import com.sksamuel.scrimage.nio.ImmutableImageLoader
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AutoCropTest : FunSpec() {
   init {
      test("autocrop should work with transparent backgrounds") {
         val image = ImmutableImageLoader.create()
            .fromResource("/com/sksamuel/scrimage/core/autocrop/1b64a74b-ae2b-417e-a7db-b238e8ec5555.png")
         image.autocrop().dimensions() shouldBe Dimension(744, 1868)
      }
   }
}
