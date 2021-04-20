package com.sksamuel.scrimage.core.color

import com.sksamuel.scrimage.color.HSVColor
import com.sksamuel.scrimage.color.RGBColor
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class HSVColorTest : StringSpec() {
   init {

      "HSV to RGB" {
         HSVColor(232F, 0.535F, 0.498F, 1.0f).toRGB() shouldBe RGBColor(59, 68, 127, 255)
         HSVColor(0f, 0F, 1F, 1f).toRGB() shouldBe RGBColor(255, 255, 255, 255)
      }
   }
}
