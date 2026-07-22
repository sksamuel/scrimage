package com.sksamuel.scrimage.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import thirdparty.colorthief.MMCQ

/**
 * Regression for integer overflow in MMCQ.VBox.avg(). The red/green/blue sums
 * were accumulated in int, but each histogram cell contributes up to
 * hval * 31.5 * MULT (MULT == 8). For a VBox holding more than ~8.5M sampled
 * pixels the running sum exceeds Integer.MAX_VALUE, so the int accumulator
 * saturates and the returned channel average is wrong. This is reachable via
 * the public ImmutableImage.quantize / ColorThief.getColorMap on large images
 * (the root VBoxes hold nearly every sampled pixel).
 */
class MMCQOverflowTest : FunSpec({

   // colour index layout used by MMCQ.getColorIndex: (r << 2*SIGBITS) + (g << SIGBITS) + b, SIGBITS = 5.
   fun colorIndex(r: Int, g: Int, b: Int) = (r shl 10) + (g shl 5) + b

   test("VBox.avg does not overflow for a box holding more than ~8.5M pixels") {
      // a single populated cell at the top of the red range (r index 31) with 50M pixels.
      val histo = IntArray(1 shl 15)
      val r = 31
      val g = 1
      val b = 1
      histo[colorIndex(r, g, b)] = 50_000_000

      val avg = MMCQ.VBox(r, r, g, g, b, b, histo).avg(false)

      // expected red = (31 + 0.5) * 8 = 252. With int overflow the sum saturated to
      // Integer.MAX_VALUE and the average collapsed to ~42.
      avg[0] shouldBe 252
      avg[1] shouldBe 12 // (1 + 0.5) * 8
      avg[2] shouldBe 12
   }
})
