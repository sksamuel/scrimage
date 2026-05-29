package com.sksamuel.scrimage.filter.instagram

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe

class CssFiltersTest : FunSpec({

   fun apply(op: CssOp, r: Float, g: Float, b: Float): FloatArray {
      val a = floatArrayOf(r, g, b)
      op.apply(a)
      return a
   }

   test("sepia(0) is the identity") {
      val o = apply(CssFilters.sepia(0f), 0.2f, 0.4f, 0.6f)
      o[0].toDouble() shouldBe (0.2 plusOrMinus 1e-4)
      o[1].toDouble() shouldBe (0.4 plusOrMinus 1e-4)
      o[2].toDouble() shouldBe (0.6 plusOrMinus 1e-4)
   }

   test("saturate(1) is the identity") {
      val o = apply(CssFilters.saturate(1f), 0.2f, 0.4f, 0.6f)
      o[0].toDouble() shouldBe (0.2 plusOrMinus 1e-4)
      o[1].toDouble() shouldBe (0.4 plusOrMinus 1e-4)
      o[2].toDouble() shouldBe (0.6 plusOrMinus 1e-4)
   }

   test("hueRotate(0) is the identity") {
      val o = apply(CssFilters.hueRotate(0f), 0.2f, 0.4f, 0.6f)
      o[0].toDouble() shouldBe (0.2 plusOrMinus 1e-4)
      o[1].toDouble() shouldBe (0.4 plusOrMinus 1e-4)
      o[2].toDouble() shouldBe (0.6 plusOrMinus 1e-4)
   }

   test("grayscale(1) collapses all channels to the same luma value") {
      val o = apply(CssFilters.grayscale(1f), 0.2f, 0.4f, 0.6f)
      o[0].toDouble() shouldBe (o[1].toDouble() plusOrMinus 1e-4)
      o[1].toDouble() shouldBe (o[2].toDouble() plusOrMinus 1e-4)
   }

   test("brightness scales and clamps to 1") {
      val o = apply(CssFilters.brightness(2f), 0.6f, 0.1f, 0.0f)
      o[0].toDouble() shouldBe (1.0 plusOrMinus 1e-4) // 1.2 clamped
      o[1].toDouble() shouldBe (0.2 plusOrMinus 1e-4)
      o[2].toDouble() shouldBe (0.0 plusOrMinus 1e-4)
   }

   test("contrast(1) is the identity") {
      val o = apply(CssFilters.contrast(1f), 0.2f, 0.4f, 0.6f)
      o[0].toDouble() shouldBe (0.2 plusOrMinus 1e-4)
      o[2].toDouble() shouldBe (0.6 plusOrMinus 1e-4)
   }
})
