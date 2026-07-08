package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import java.awt.Color

class VignetteFilterRangeTest : FunSpec({

   // Regression: VignetteFilter validated start/blur via `assert`, which is
   // disabled by default in production JVMs. Out-of-range start used to
   // surface as a cryptic IllegalArgumentException from RadialGradientPaint
   // about gradient fractions; out-of-range blur was passed to
   // GaussianBlurFilter as a negative radius. Now both reject up front
   // with a clear IllegalArgumentException naming the offending value.
   //
   // end was not validated at all: end <= 0 surfaced at apply time as a
   // cryptic "Radius must be greater than zero" from RadialGradientPaint
   // (the gradient radius is image.radius() * end). It now fails fast in
   // the constructor too, with (0, 1] as the valid range.

   test("constructor rejects start below 0") {
      val ex = shouldThrow<IllegalArgumentException> {
         VignetteFilter(-0.1f, 0.95f, 0.3f, Color.BLACK)
      }
      ex.message!!.shouldContain("start")
      ex.message!!.shouldContain("-0.1")
   }

   test("constructor rejects start above 1") {
      val ex = shouldThrow<IllegalArgumentException> {
         VignetteFilter(1.5f, 0.95f, 0.3f, Color.BLACK)
      }
      ex.message!!.shouldContain("start")
      ex.message!!.shouldContain("1.5")
   }

   test("constructor rejects blur below 0") {
      val ex = shouldThrow<IllegalArgumentException> {
         VignetteFilter(0.5f, 0.95f, -0.2f, Color.BLACK)
      }
      ex.message!!.shouldContain("blur")
      ex.message!!.shouldContain("-0.2")
   }

   test("constructor rejects blur above 1") {
      val ex = shouldThrow<IllegalArgumentException> {
         VignetteFilter(0.5f, 0.95f, 2.0f, Color.BLACK)
      }
      ex.message!!.shouldContain("blur")
      ex.message!!.shouldContain("2.0")
   }

   test("constructor accepts boundary values 0 and 1") {
      // Both 0 and 1 are valid (inclusive bounds).
      VignetteFilter(0f, 0.95f, 0f, Color.BLACK)
      VignetteFilter(1f, 0.95f, 1f, Color.BLACK)
   }

   test("constructor rejects end of 0") {
      val ex = shouldThrow<IllegalArgumentException> {
         VignetteFilter(0.85f, 0f, 0.3f, Color.BLACK)
      }
      ex.message!!.shouldContain("end")
      ex.message!!.shouldContain("0.0")
   }

   test("constructor rejects negative end") {
      val ex = shouldThrow<IllegalArgumentException> {
         VignetteFilter(0.85f, -0.5f, 0.3f, Color.BLACK)
      }
      ex.message!!.shouldContain("end")
      ex.message!!.shouldContain("-0.5")
   }

   test("constructor rejects end above 1") {
      val ex = shouldThrow<IllegalArgumentException> {
         VignetteFilter(0.85f, 1.5f, 0.3f, Color.BLACK)
      }
      ex.message!!.shouldContain("end")
      ex.message!!.shouldContain("1.5")
   }

   test("default construction and valid end values still apply cleanly") {
      val image = ImmutableImage.create(32, 32)
      image.filter(VignetteFilter())
      image.filter(VignetteFilter(0.85f, 0.95f, 0.3f, Color.BLACK))
      image.filter(VignetteFilter(0.5f, 1f, 0.2f, Color.RED)) // end = 1 boundary
   }
})
