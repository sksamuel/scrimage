package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ComponentColorModel
import java.awt.image.DataBuffer

/**
 * Regression test for empty() and blank() crashing on TYPE_CUSTOM sources.
 *
 * BufferedImage.getType() returns 0 (TYPE_CUSTOM) for any image
 * constructed from a custom Raster + ColorModel that doesn't match a
 * predefined type. The previous implementations of empty() and blank()
 * passed that 0 into the BufferedImage(int, int, int) constructor, which
 * throws "Unknown image type 0".
 *
 * The fix falls back to TYPE_INT_ARGB (matching fromAwt's existing
 * policy) so empty() / blank() never crash on a valid input image.
 */
class EmptyBlankTypeCustomTest : FunSpec({

   // Build a TYPE_CUSTOM BufferedImage. 16-bit-per-channel RGB has no
   // predefined BufferedImage type, so wrapping a USHORT-based RGB raster
   // in a ComponentColorModel produces TYPE_CUSTOM.
   fun customTypeImage(w: Int, h: Int): BufferedImage {
      val cs = ColorSpace.getInstance(ColorSpace.CS_sRGB)
      val cm = ComponentColorModel(cs, intArrayOf(16, 16, 16), false, false,
         java.awt.Transparency.OPAQUE, DataBuffer.TYPE_USHORT)
      val raster = cm.createCompatibleWritableRaster(w, h)
      return BufferedImage(cm, raster, false, null)
   }

   test("source has type 0 (TYPE_CUSTOM) — sanity check fixture") {
      customTypeImage(2, 2).type shouldBe BufferedImage.TYPE_CUSTOM
   }

   test("empty() does not throw on a TYPE_CUSTOM source") {
      val image = ImmutableImage.wrapAwt(customTypeImage(3, 4))
      val blank = image.empty()
      blank.width shouldBe 3
      blank.height shouldBe 4
      // Falls back to TYPE_INT_ARGB rather than dying with
      // "Unknown image type 0"
      blank.awt().type shouldBe BufferedImage.TYPE_INT_ARGB
   }

   test("blank() does not throw on a TYPE_CUSTOM source") {
      val image = ImmutableImage.wrapAwt(customTypeImage(2, 5))
      val blank = image.blank()
      blank.width shouldBe 2
      blank.height shouldBe 5
      blank.awt().type shouldBe BufferedImage.TYPE_INT_ARGB
   }

   test("empty() preserves dimensions and AWT type for non-custom sources") {
      val image = ImmutableImage.create(7, 4, BufferedImage.TYPE_INT_ARGB)
      val blank = image.empty()
      blank.width shouldBe 7
      blank.height shouldBe 4
      blank.awt().type shouldBe BufferedImage.TYPE_INT_ARGB
   }

   test("blank() preserves the source's AWT type for non-custom sources") {
      val image = ImmutableImage.create(2, 2, BufferedImage.TYPE_INT_RGB)
      val blank = image.blank()
      blank.awt().type shouldBe BufferedImage.TYPE_INT_RGB
   }
})
