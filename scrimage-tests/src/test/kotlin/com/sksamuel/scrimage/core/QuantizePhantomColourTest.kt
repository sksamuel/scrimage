package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Regression test: MMCQ's median cut can leave a VBox with zero pixels (the half of the
 * colour space that no pixel falls into when the image has fewer distinct colours than the
 * requested palette size). VBox.avg() returned that empty box's geometric midpoint, so a
 * single-colour image quantized to a 2-colour palette reported a second "phantom" colour
 * that does not exist anywhere in the image.
 */
class QuantizePhantomColourTest : FunSpec({

   test("quantize(2) on a single-colour image returns exactly one colour") {
      val palette = ImmutableImage.filled(100, 100, Color(200, 30, 30)).quantize(2)
      palette shouldHaveSize 1
      // The one colour is the real colour (within MMCQ's 5-bit bucket precision).
      val c = palette[0]
      (c.red in 192..207) shouldBe true
      (c.green in 24..39) shouldBe true
      (c.blue in 24..39) shouldBe true
   }

   test("quantize(4) on a single-colour image returns exactly one colour") {
      ImmutableImage.filled(100, 100, Color(200, 30, 30)).quantize(4) shouldHaveSize 1
   }

   test("quantize(2) on a two-colour image returns exactly two colours") {
      // top half blue, bottom half red (vary by row: the sampler reads every 10th flat index)
      val image = ImmutableImage.filled(10, 10, Color.BLUE).map { p ->
         if (p.y < 5) Color.BLUE else Color.RED
      }
      val palette = image.quantize(2).map { it.toHex() }.toSet()
      palette shouldHaveSize 2
      palette.any { it.startsWith("0404") } shouldBe true   // blue-ish
      palette.any { it.endsWith("0404") } shouldBe true     // red-ish
   }
})
