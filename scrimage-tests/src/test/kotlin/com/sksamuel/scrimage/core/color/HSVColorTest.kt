package com.sksamuel.scrimage.core.color

import com.sksamuel.scrimage.color.HSVColor
import com.sksamuel.scrimage.color.RGBColor
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class HSVColorTest : StringSpec() {
   init {

      "HSV to RGB" {
         HSVColor(232F, 0.535F, 0.498F, 1.0f).toRGB() shouldBe RGBColor(59, 68, 127, 255)
         HSVColor(0f, 0F, 1F, 1f).toRGB() shouldBe RGBColor(255, 255, 255, 255)
      }

      "valid components are accepted (hue in [0, 360], others in [0, 1])" {
         val color = HSVColor(360f, 0.5f, 1.0f, 0.25f)
         color.hue shouldBe 360f
         color.saturation shouldBe 0.5f
         color.value shouldBe 1.0f
         color.alpha shouldBe 0.25f
      }

      "out-of-range components are rejected with IllegalArgumentException" {
         shouldThrow<IllegalArgumentException> { HSVColor(-1f, 0f, 0f, 1f) }
         shouldThrow<IllegalArgumentException> { HSVColor(361f, 0f, 0f, 1f) }
         shouldThrow<IllegalArgumentException> { HSVColor(0f, -0.1f, 0f, 1f) }
         shouldThrow<IllegalArgumentException> { HSVColor(0f, 1.1f, 0f, 1f) }
         shouldThrow<IllegalArgumentException> { HSVColor(0f, 0f, 1.1f, 1f) }
         shouldThrow<IllegalArgumentException> { HSVColor(0f, 0f, 0f, 1.1f) }
      }

      // NaN compares false against any bound, so a naive `value < 0 || value > max` check
      // lets it through and toRGB() silently produces a garbage colour.
      "NaN components are rejected with IllegalArgumentException" {
         shouldThrow<IllegalArgumentException> { HSVColor(Float.NaN, 0f, 0f, 1f) }
         shouldThrow<IllegalArgumentException> { HSVColor(0f, Float.NaN, 0f, 1f) }
         shouldThrow<IllegalArgumentException> { HSVColor(0f, 0f, Float.NaN, 1f) }
         shouldThrow<IllegalArgumentException> { HSVColor(0f, 0f, 0f, Float.NaN) }
      }
   }
}
