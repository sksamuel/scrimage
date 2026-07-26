package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage

/**
 * Regression test for copy() losing channel precision when alpha != 255.
 *
 * The original AwtImage.clone(int) used Graphics2D.drawImage to populate
 * the target buffer. Graphics2D.drawImage composites via SrcOver, which
 * premultiplies alpha into each colour channel and then unpremultiplies
 * on read-back. For alpha values that don't divide 255 cleanly, the
 * round-trip rounds away ±1 from each channel.
 *
 * Concrete example: a pixel with (r=100, g=150, b=200, a=128) became
 * (r=100, g=149, b=199, a=128) after copy().
 *
 * The fix uses BufferedImage.copyData when the source and target image
 * types match — a direct raster copy that doesn't go through compositing.
 */
class CopyPrecisionTest : FunSpec({

   test("copy preserves all channels exactly when alpha != 255 (TYPE_INT_ARGB)") {
      val pixels = arrayOf(Pixel(0, 0, 100, 150, 200, 128))
      val original = ImmutableImage.create(1, 1, pixels)
      val copy = original.copy()
      val p = copy.pixel(0, 0)
      p.red() shouldBe 100
      p.green() shouldBe 150
      p.blue() shouldBe 200
      p.alpha() shouldBe 128
   }

   test("copy preserves channels across multiple awkward alpha values") {
      // A spread of alphas that previously rounded channels by ±1.
      val pixels = arrayOf(
         Pixel(0, 0, 100, 150, 200, 1),
         Pixel(1, 0, 100, 150, 200, 7),
         Pixel(0, 1, 100, 150, 200, 64),
         Pixel(1, 1, 100, 150, 200, 200)
      )
      val original = ImmutableImage.create(2, 2, pixels)
      val copy = original.copy()
      for (y in 0 until 2) for (x in 0 until 2) {
         val src = original.pixel(x, y)
         val dst = copy.pixel(x, y)
         dst.red() shouldBe src.red()
         dst.green() shouldBe src.green()
         dst.blue() shouldBe src.blue()
         dst.alpha() shouldBe src.alpha()
      }
   }

   test("copy is independent of the source — mutating the copy does not affect the original") {
      val pixels = arrayOf(Pixel(0, 0, 50, 60, 70, 200))
      val original = ImmutableImage.create(1, 1, pixels)
      val copy = original.copy()
      copy.setPixel(Pixel(0, 0, 0, 0, 0, 0))
      // Original must still hold its values
      original.pixel(0, 0).red() shouldBe 50
      original.pixel(0, 0).alpha() shouldBe 200
   }

   test("copy(type) into a different type still uses Graphics2D conversion") {
      // For a TYPE_INT_RGB target the alpha channel cannot be represented;
      // the copy collapses alpha to opaque (which is the correct semantic).
      val pixels = arrayOf(Pixel(0, 0, 100, 150, 200, 128))
      val original = ImmutableImage.create(1, 1, pixels)
      val converted = original.copy(BufferedImage.TYPE_INT_RGB)
      converted.awt().type shouldBe BufferedImage.TYPE_INT_RGB
      converted.pixel(0, 0).alpha() shouldBe 255
   }

   // The same-type copyData fast path must not be combined with a target created via
   // new BufferedImage(w, h, type): for palette-based types that target gets the *default*
   // palette, and copyData copies raw palette indices, so every pixel is remapped through
   // the wrong palette. The target must reuse the source's own IndexColorModel.
   test("copy preserves colors for palette-based images (GIF, TYPE_BYTE_INDEXED)") {
      val original = ImmutableImage.loader().fromStream(javaClass.getResourceAsStream("/github174.gif"))
      original.awt().type shouldBe BufferedImage.TYPE_BYTE_INDEXED
      val copy = original.copy()
      copy.awt().type shouldBe BufferedImage.TYPE_BYTE_INDEXED
      copy.argbints() shouldBe original.argbints()
   }

   test("copy preserves colors for an indexed image with a custom palette") {
      val palette = intArrayOf(0xFF112233.toInt(), 0xFF446688.toInt(), 0xFFAABBCC.toInt(), 0xFF010203.toInt())
      val icm = java.awt.image.IndexColorModel(8, 4, palette, 0, false, -1, java.awt.image.DataBuffer.TYPE_BYTE)
      val buf = BufferedImage(4, 4, BufferedImage.TYPE_BYTE_INDEXED, icm)
      for (y in 0 until 4) for (x in 0 until 4) buf.setRGB(x, y, palette[(x + y) % 4])
      val original = ImmutableImage.wrapAwt(buf)
      val copy = original.copy()
      copy.awt().type shouldBe BufferedImage.TYPE_BYTE_INDEXED
      copy.argbints() shouldBe original.argbints()
   }

   test("copy preserves channels for TYPE_4BYTE_ABGR with non-255 alpha") {
      // Same precision invariant must hold for non-int-buffer image types.
      val buf = BufferedImage(2, 2, BufferedImage.TYPE_4BYTE_ABGR)
      buf.setRGB(0, 0, 0x80FF6432.toInt()) // alpha=128, r=255, g=100, b=50
      buf.setRGB(1, 0, 0x40_64C896.toInt())
      buf.setRGB(0, 1, 0xFF_00_FF_00.toInt())
      buf.setRGB(1, 1, 0x01_FF_FF_FF.toInt()) // alpha=1, white
      val original = ImmutableImage.wrapAwt(buf)
      val copy = original.copy()
      for (y in 0 until 2) for (x in 0 until 2) {
         copy.pixel(x, y).argb shouldBe original.pixel(x, y).argb
      }
   }
})
