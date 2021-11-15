package com.sksamuel.scrimage.core.autocrop

import com.sksamuel.scrimage.Dimension
import com.sksamuel.scrimage.nio.ImmutableImageLoader
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.awt.Color

class AutoCropTest : FunSpec() {
   init {

      test("autocrop should work with transparent backgrounds") {
         val image = ImmutableImageLoader.create()
            .fromResource("/com/sksamuel/scrimage/core/autocrop/1b64a74b-ae2b-417e-a7db-b238e8ec5555.png")
         image.autocrop().dimensions() shouldBe Dimension(744, 1868)
      }

      test("autocrop with no matching content should return the same image") {
         val image = ImmutableImageLoader.create()
            .fromResource("/com/sksamuel/scrimage/core/autocrop/1b64a74b-ae2b-417e-a7db-b238e8ec5555.png")
         image.autocrop(Color.ORANGE).shouldBeSameInstanceAs(image)
      }
   }
}
