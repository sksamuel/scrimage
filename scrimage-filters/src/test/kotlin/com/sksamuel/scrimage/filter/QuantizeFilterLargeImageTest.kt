package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Regression: OctTreeQuantizer accumulated per-node channel totals in ints.
 * A single colour covering more than 2^31 / 255 (about 8.4 million) pixels
 * overflowed the total, so a large uniform white image quantized to a wrong
 * colour (e.g. (255, 255, 34) for 3000x3000 white). The totals are now longs.
 */
class QuantizeFilterLargeImageTest : FunSpec({

   test("QuantizeFilter keeps the colour of a large uniform image") {
      val img = ImmutableImage.filled(3000, 3000, Color.WHITE)
      val p = img.filter(QuantizeFilter(8)).pixel(100, 100)
      "${p.red()},${p.green()},${p.blue()}" shouldBe "255,255,255"
   }
})
