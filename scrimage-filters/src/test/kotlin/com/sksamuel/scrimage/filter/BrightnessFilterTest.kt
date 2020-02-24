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

})
