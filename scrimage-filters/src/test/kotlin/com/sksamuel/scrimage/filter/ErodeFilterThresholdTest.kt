package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.awt.Color

/**
 * Verifies the threshold constructor parameter is passed through to the jhlabs
 * ErodeFilter (setThreshold) — the neighbour count required for erosion, so it
 * changes the result.
 */
class ErodeFilterThresholdTest : FunSpec({

   // a binary checkerboard of 4x4 blocks
   val src = ImmutableImage.create(48, 48).map { p ->
      if (((p.x() / 4) + (p.y() / 4)) % 2 == 0) Color.WHITE else Color.BLACK
   }

   test("threshold is passed through") {
      val a = src.filter(ErodeFilter(1, 1))
      val b = src.filter(ErodeFilter(1, 8))
      a shouldNotBe b
   }

   test("the iterations-only constructor still applies") {
      src.filter(ErodeFilter(2)).width shouldBe 48
   }
})
