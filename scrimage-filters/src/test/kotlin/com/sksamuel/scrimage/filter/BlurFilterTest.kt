@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BlurFilterTest : FunSpec({

   val folder = "/com/sksamuel/scrimage/filters/blur"

   test("filter output matches expected") {
      ImmutableImage.fromResource("/bird_small.png").filter(BlurFilter()) shouldBe
         ImmutableImage.fromResource("$folder/bird_small_blur.png")

      ImmutableImage.fromResource("/caviar.jpg").filter(BlurFilter()) shouldBe
         ImmutableImage.fromResource("$folder/caviar_blur.png")

      ImmutableImage.fromResource("/love.jpg").filter(BlurFilter()) shouldBe
         ImmutableImage.fromResource("$folder/love_blur.png")

      ImmutableImage.fromResource("/masks.jpg").filter(BlurFilter()) shouldBe
         ImmutableImage.fromResource("$folder/masks_blur.png")
   }

})
