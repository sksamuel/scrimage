@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BlackThresholdFilterTest : FunSpec({

   test("black threshold filter output matches expected") {

      val image = ImmutableImage.fromStream(javaClass.getResourceAsStream("/bird_small.png"))
      val thresholdPercentage = 40.0

      BlackThresholdFilter(thresholdPercentage).apply(image)

      val expected =
         ImmutableImage.fromStream(javaClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_black_threshold.png"))
      image shouldBe expected
   }

})
