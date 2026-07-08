package com.sksamuel.scrimage.core.color

import com.sksamuel.scrimage.color.CMYKColor
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class CMYKColorTest : StringSpec({

   "valid components in [0, 1] are accepted" {
      val color = CMYKColor(0.0f, 0.5f, 1.0f, 0.25f, 0.75f)
      color.c shouldBe 0.0f
      color.m shouldBe 0.5f
      color.y shouldBe 1.0f
      color.k shouldBe 0.25f
      color.alpha shouldBe 0.75f
   }

   "out-of-range components are rejected with IllegalArgumentException" {
      shouldThrow<IllegalArgumentException> { CMYKColor(-0.1f, 0f, 0f, 0f, 1f) }
      shouldThrow<IllegalArgumentException> { CMYKColor(1.1f, 0f, 0f, 0f, 1f) }
      shouldThrow<IllegalArgumentException> { CMYKColor(0f, -0.1f, 0f, 0f, 1f) }
      shouldThrow<IllegalArgumentException> { CMYKColor(0f, 0f, 1.1f, 0f, 1f) }
      shouldThrow<IllegalArgumentException> { CMYKColor(0f, 0f, 0f, -0.1f, 1f) }
      shouldThrow<IllegalArgumentException> { CMYKColor(0f, 0f, 0f, 0f, 1.1f) }
   }

   // NaN compares false against any bound, so a naive `value < 0 || value > 1` check lets it
   // through and toRGB() silently produces a garbage color.
   "NaN components are rejected with IllegalArgumentException" {
      shouldThrow<IllegalArgumentException> { CMYKColor(Float.NaN, 0f, 0f, 0f, 1f) }
      shouldThrow<IllegalArgumentException> { CMYKColor(0f, Float.NaN, 0f, 0f, 1f) }
      shouldThrow<IllegalArgumentException> { CMYKColor(0f, 0f, Float.NaN, 0f, 1f) }
      shouldThrow<IllegalArgumentException> { CMYKColor(0f, 0f, 0f, Float.NaN, 1f) }
      shouldThrow<IllegalArgumentException> { CMYKColor(0f, 0f, 0f, 0f, Float.NaN) }
   }
})
