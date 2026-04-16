@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BrightnessFilterTest : FunSpec({

   val original = ImmutableImage.fromStream(javaClass.getResourceAsStream("/bird_small.png"))

   test("filter output matches expected") {
      val expected =
         ImmutableImage.fromStream(javaClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_brighten.png"))
      original.filter(BrightnessFilter(1.4f)) shouldBe expected
   }

   // Regression: filter() set target = this when the image type matched the filter's
   // required types (or types() returned empty, the common case). BufferedOpFilter
   // applies the op in-place via op().filter(image.awt(), image.awt()), so the
   // "immutable" original was silently mutated.
   test("filter does not mutate the original image") {
      val snapshot = original.copy()
      original.filter(BrightnessFilter(1.4f))
      original shouldBe snapshot
   }

})
