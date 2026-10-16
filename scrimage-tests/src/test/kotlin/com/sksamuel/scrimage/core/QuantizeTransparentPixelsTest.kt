package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage

/**
 * Regression test: ColorThief filtered out fully transparent pixels (alpha < 125)
 * only on the TYPE_4BYTE_ABGR byte fast path. The TYPE_INT_ARGB int fast path and
 * the generic getRGB slow path ignored alpha entirely, so a transparent background
 * (0x00000000) was counted as opaque black. Scrimage's canonical type is
 * TYPE_INT_ARGB, so transparent-background PNGs reported black as a dominant colour.
 */
class QuantizeTransparentPixelsTest : FunSpec({

   // 10x10, top half fully transparent, bottom half opaque red.
   fun image(type: Int): ImmutableImage {
      val bi = BufferedImage(10, 10, type)
      for (y in 0 until 10) {
         for (x in 0 until 10) {
            bi.setRGB(x, y, if (y < 5) 0x00000000 else 0xFFFF0000.toInt())
         }
      }
      return ImmutableImage.wrapAwt(bi)
   }

   fun isNearBlack(c: com.sksamuel.scrimage.color.RGBColor) = c.red < 32 && c.green < 32 && c.blue < 32

   test("quantize on TYPE_INT_ARGB ignores fully transparent pixels like TYPE_4BYTE_ABGR") {
      val argb = image(BufferedImage.TYPE_INT_ARGB).quantize(2).map { it.toHex() }
      val abgr = image(BufferedImage.TYPE_4BYTE_ABGR).quantize(2).map { it.toHex() }
      argb shouldBe abgr
   }

   test("quantize on TYPE_INT_ARGB does not report transparent background as black") {
      val palette = image(BufferedImage.TYPE_INT_ARGB).quantize(2)
      palette.filter { isNearBlack(it) } shouldHaveSize 0
   }

   test("quantize via the slow path (TYPE_INT_ARGB_PRE) does not report transparent background as black") {
      val palette = image(BufferedImage.TYPE_INT_ARGB_PRE).quantize(2)
      palette.filter { isNearBlack(it) } shouldHaveSize 0
   }

   test("quantize on an opaque TYPE_INT_RGB image is unaffected") {
      val bi = BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)
      for (y in 0 until 10) for (x in 0 until 10) bi.setRGB(x, y, if (y < 5) 0x000000 else 0xFF0000)
      val palette = ImmutableImage.wrapAwt(bi).quantize(2)
      // black is a genuine opaque colour here and must be kept
      palette.count { isNearBlack(it) } shouldBe 1
   }
})
