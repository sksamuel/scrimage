package com.sksamuel.scrimage.core.color

import com.sksamuel.scrimage.color.Grayscale
import com.sksamuel.scrimage.color.RGBColor
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class GrayscaleTest : StringSpec({

   "Grayscale converts to RGB by repeating the gray value across channels" {
      Grayscale(128).toRGB() shouldBe RGBColor(128, 128, 128, 255)
      Grayscale(64, 200).toRGB() shouldBe RGBColor(64, 64, 64, 200)
   }

   // Mirrors the RGBColor validation: an out-of-range component must fail fast at
   // construction rather than being stored unchecked and only failing later in toRGB().
   "Grayscale rejects out-of-range gray" {
      shouldThrow<IllegalArgumentException> { Grayscale(300) }
      shouldThrow<IllegalArgumentException> { Grayscale(-1) }
      shouldThrow<IllegalArgumentException> { Grayscale(256, 255) }
   }

   "Grayscale rejects out-of-range alpha" {
      shouldThrow<IllegalArgumentException> { Grayscale(0, -1) }
      shouldThrow<IllegalArgumentException> { Grayscale(0, 256) }
   }

   "Grayscale accepts the boundary values" {
      Grayscale(0, 0).toRGB() shouldBe RGBColor(0, 0, 0, 0)
      Grayscale(255, 255).toRGB() shouldBe RGBColor(255, 255, 255, 255)
   }
})
