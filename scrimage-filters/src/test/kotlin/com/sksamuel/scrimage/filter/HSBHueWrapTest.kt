package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeLessThan

/**
 * Regression: HSBAdjustFilter's negative-hue wrap added Math.PI*2 to
 * a hue stored in `[0, 1)`. Adding ~6.28 to a normalised hue and
 * handing it to HSBtoRGB (which internally takes `h - floor(h)`)
 * produced an arbitrary offset of ~0.283, not the intended wrap
 * back into [0, 1). For pure red (hue=0) with hFactor=-0.1 the
 * expected post-shift hue is 0.9 (a magenta); the bug produced a
 * hue ~0.18 (orange).
 *
 * The fix replaces the loop with `hsb[0] = hsb[0] - floor(hsb[0])`.
 */
class HSBHueWrapTest : FunSpec({

   fun pixelDist(a: com.sksamuel.scrimage.pixels.Pixel, b: com.sksamuel.scrimage.pixels.Pixel): Double {
      val dr = (a.red() - b.red()).toDouble()
      val dg = (a.green() - b.green()).toDouble()
      val db = (a.blue() - b.blue()).toDouble()
      return kotlin.math.sqrt(dr * dr + dg * dg + db * db)
   }

   test("negative hFactor of -0.1 from pure red lands near magenta, not orange") {
      // Pure red is at hue 0. With a -0.1 hue shift the expected hue
      // is 0.9, which in RGB is a magenta — approximately (255, 0, 153).
      // Pre-fix the result was an orange-ish (255, ~70, 0).
      val red = ImmutableImage.filled(8, 8, java.awt.Color.RED)
      val shifted = red.filter(HSBFilter(-0.1f, 0f, 0f))

      val out = shifted.pixel(0, 0)
      val expectedMagenta = com.sksamuel.scrimage.pixels.Pixel(0, 0, 255, 0, 153, 255)
      val buggyOrange = com.sksamuel.scrimage.pixels.Pixel(0, 0, 255, 70, 0, 255)

      // The post-fix output should be much closer to the expected
      // magenta than to the orange that the pre-fix code produced.
      pixelDist(out, expectedMagenta) shouldBeLessThan pixelDist(out, buggyOrange)
   }

   test("hFactor of +1.0 returns to the same colour (full hue cycle)") {
      // A full +1.0 hue cycle should round-trip the image: hue + 1
      // mod 1 = hue. With the buggy loop +Math.PI*2 wrap, this only
      // touched the negative path, but the round-trip exercises the
      // floor() branch too.
      val red = ImmutableImage.filled(8, 8, java.awt.Color.RED)
      val rotated = red.filter(HSBFilter(1.0f, 0f, 0f))
      val out = rotated.pixel(0, 0)
      val rgb = com.sksamuel.scrimage.pixels.Pixel(0, 0, 255, 0, 0, 255)
      pixelDist(out, rgb) shouldBeLessThan 5.0
   }
})
