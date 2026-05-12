package com.sksamuel.scrimage.canvas

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.canvas.painters.RandomPainter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.shouldBe

/**
 * Regression test for RandomPainter, which had at least four bugs in
 * its getRaster loop:
 *
 *  1. the outer loop wrote `x++` instead of `x2++` and the inner loop
 *     wrote `y++` instead of `y2++`, so only one cell was ever touched
 *     and the loops ran an arbitrary (parameter-dependent) number of
 *     times;
 *  2. it called raster.setPixel(x2, y2, ...) using the GLOBAL coords
 *     rather than raster-local (0..w, 0..h) — would have thrown AIOOB
 *     had the loops advanced;
 *  3. it built the channel array as [alpha, red, green, blue] but the
 *     default RGB ColorModel expects [red, green, blue, alpha], so the
 *     channel order was wrong;
 *  4. it set alpha=0 on every pixel even though the Paint advertised
 *     Transparency.OPAQUE.
 *
 * After the fix:
 *  - every pixel is filled
 *  - every pixel is opaque (alpha=255)
 *  - colours vary across pixels (the "random" property)
 */
class RandomPainterTest : FunSpec({

   test("RandomPainter fills every pixel of the image with opaque colour") {
      val image = ImmutableImage.create(40, 40).fill(RandomPainter())
      // Every pixel should be opaque (was alpha=0 before the fix).
      val alphas = image.pixels().map { it.alpha() }.toSet()
      alphas shouldBe setOf(255)
   }

   test("RandomPainter produces varying colours across pixels") {
      val image = ImmutableImage.create(40, 40).fill(RandomPainter())
      // Distinct colours — well above 1, since random over a 1600-pixel area.
      // Before the fix only one pixel was ever written so most pixels would
      // share the (zero-initialised) background colour.
      val distinct = image.pixels().map { it.argb }.toSet()
      distinct shouldHaveAtLeastSize 100
   }
})
