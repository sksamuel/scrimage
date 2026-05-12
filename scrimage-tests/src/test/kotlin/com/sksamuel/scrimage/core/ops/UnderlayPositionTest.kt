package com.sksamuel.scrimage.core.ops

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

class UnderlayPositionTest : FunSpec({

   // Regression for underlay(image, x, y) drawing both `this` and the
   // underlay at the same (x, y). The contract — and the docstring — say
   // the underlay sits at the back and `this` sits on top, with (x, y)
   // determining where the (0, 0) of the top layer is placed.
   //
   // The zero-arg `underlay(image)` reduces to (0, 0) for both, masking
   // the bug. Anyone using the explicit (x, y) overload saw the underlay
   // offset to the same position as the top layer instead of covering the
   // canvas, leaving the area before (x, y) as blank pixels.

   test("underlay(image, x, y) places the underlay at the canvas origin and `this` at (x, y)") {
      val red = ImmutableImage.filled(100, 100, Color.RED)
      val blue = ImmutableImage.filled(100, 100, Color.BLUE)

      // Pull `this` (red) down/right by (40, 40); the underlay (blue) must
      // cover the entire canvas first.
      val out = red.underlay(blue, 40, 40)

      // (0, 0) is in the area not covered by `this` after the offset, so
      // it must be the underlay colour (blue).
      val topLeft = out.pixel(0, 0)
      topLeft.red() shouldBe 0
      topLeft.green() shouldBe 0
      topLeft.blue() shouldBe 255

      // (50, 50) is inside the offset top layer, so it must be `this` (red).
      val middle = out.pixel(50, 50)
      middle.red() shouldBe 255
      middle.green() shouldBe 0
      middle.blue() shouldBe 0

      // (99, 99) — bottom-right — is also inside the offset top layer.
      val bottomRight = out.pixel(99, 99)
      bottomRight.red() shouldBe 255
      bottomRight.green() shouldBe 0
      bottomRight.blue() shouldBe 0
   }

   test("zero-arg underlay still covers the canvas") {
      val red = ImmutableImage.filled(20, 20, Color.RED)
      val blue = ImmutableImage.filled(20, 20, Color.BLUE)
      val out = red.underlay(blue)
      // With both at (0, 0) the top layer covers the underlay completely.
      out.pixel(0, 0).red() shouldBe 255
      out.pixel(19, 19).red() shouldBe 255
   }
})
