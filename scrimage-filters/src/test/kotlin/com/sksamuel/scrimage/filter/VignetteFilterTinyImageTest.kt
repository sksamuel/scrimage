package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Regression: VignetteFilter computed the gradient radius as
 * `image.radius() * end`, and AwtImage.radius() truncates sqrt(0.5) to 0 for
 * a 1x1 image, so RadialGradientPaint threw "Radius must be greater than
 * zero". SummerFilter(true) and NashvilleFilter wrap a VignetteFilter and
 * crashed the same way. The radius is now floored at 1px.
 */
class VignetteFilterTinyImageTest : FunSpec({

   val sizes = listOf(1 to 1, 2 to 1, 1 to 2, 2 to 2)

   test("VignetteFilter does not throw on tiny images") {
      sizes.forEach { (w, h) ->
         val out = ImmutableImage.filled(w, h, Color.RED).filter(VignetteFilter())
         out.width shouldBe w
         out.height shouldBe h
      }
   }

   test("SummerFilter with vignette does not throw on tiny images") {
      sizes.forEach { (w, h) ->
         ImmutableImage.filled(w, h, Color.RED).filter(SummerFilter(true)).width shouldBe w
      }
   }

   test("NashvilleFilter does not throw on tiny images") {
      sizes.forEach { (w, h) ->
         ImmutableImage.filled(w, h, Color.RED).filter(NashvilleFilter()).width shouldBe w
      }
   }

   test("VignetteFilter on a normal image still darkens the corners more than the centre") {
      val out = ImmutableImage.filled(100, 100, Color.WHITE).filter(VignetteFilter())
      (out.pixel(0, 0).red() < out.pixel(50, 50).red()) shouldBe true
   }
})
