@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.filter

import java.awt.Color

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BorderFilterTest : FunSpec({

   val bird = ImmutableImage.fromResource("/bird_small.png")
   val love = ImmutableImage.fromResource("/love.jpg")
   val masks = ImmutableImage.fromResource("/masks.jpg")

   test("filter output matches expected") {
      val expectedb = ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/border/bird_small_border.png")
      bird.filter(BorderFilter(8)) shouldBe expectedb
   }

   test("should support multiple border levels") {
      masks.filter(BorderFilter(2)) shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/border/masks_border_2.png")
      masks.filter(BorderFilter(6)) shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/border/masks_border_6.png")
      masks.filter(BorderFilter(13)) shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/border/masks_border_13.png")
   }

   test("should support multiple border colours") {
      love.filter(BorderFilter(5, Color.GREEN)) shouldBe
         ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/border/love_border_green.png")
      love.filter(BorderFilter(5, Color.BLUE)) shouldBe
         ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/border/love_border_blue.png")
      love.filter(BorderFilter(5, Color.RED)) shouldBe
         ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/border/love_border_red.png")
   }

})
