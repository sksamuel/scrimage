package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.filter.Filter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.IndexColorModel

/**
 * Regression tests for colour-changing operations on palette-based (IndexColorModel)
 * images: toGrayscale, invert, gamma, contrast, map, brightness, removeTransparency
 * and filter(Filter).
 *
 * These all produced their result by copying the source in its own palette type and
 * writing the transformed colours back with setRGB. A palette image can only store the
 * entries of its palette, so every transformed colour was replaced by the nearest
 * ORIGINAL colour: toGrayscale() of a GIF whose palette has no gray returned no gray
 * pixels at all, invert() and gamma() returned (near) the source colours, and
 * brightness() on a 4-colour palette returned the source unchanged.
 *
 * Palette-based images are therefore promoted to TYPE_INT_ARGB by these operations,
 * and must produce exactly the result of the same operation on copy(TYPE_INT_ARGB).
 */
class PaletteColourTransformsTest : FunSpec({

   // off-cube colours: none of these are in Java's default TYPE_BYTE_INDEXED palette,
   // none of them is gray
   fun indexedFixture(w: Int = 6, h: Int = 4, transparentIndex: Int = -1): ImmutableImage {
      val r = byteArrayOf(123.toByte(), 10.toByte(), 200.toByte(), 77.toByte())
      val g = byteArrayOf(45.toByte(), 200.toByte(), 100.toByte(), 77.toByte())
      val b = byteArrayOf(67.toByte(), 30.toByte(), 250.toByte(), 77.toByte())
      val icm = IndexColorModel(8, 4, r, g, b, transparentIndex)
      val buf = BufferedImage(icm, icm.createCompatibleWritableRaster(w, h), false, null)
      for (y in 0 until h) for (x in 0 until w) buf.raster.setSample(x, y, 0, (x + y) % 4)
      return ImmutableImage.wrapAwt(buf)
   }

   fun gif(): ImmutableImage = ImmutableImage.loader().fromResource("/github174.gif")

   fun ImmutableImage.argbList(): List<Int> = argbints().toList()

   fun isGray(p: Int): Boolean {
      val r = (p shr 16) and 0xFF
      val g = (p shr 8) and 0xFF
      val b = p and 0xFF
      return r == g && g == b
   }

   fun ImmutableImage.shouldBePromotedToArgb() {
      awt().type shouldBe BufferedImage.TYPE_INT_ARGB
   }

   test("toGrayscale on a custom-palette indexed image yields gray pixels equal to the ARGB result") {
      val img = indexedFixture()
      val out = img.toGrayscale()
      out.shouldBePromotedToArgb()
      out.argbList().all { isGray(it) } shouldBe true
      out.argbList() shouldBe img.copy(BufferedImage.TYPE_INT_ARGB).toGrayscale().argbList()
   }

   test("invert on a custom-palette indexed image inverts each channel") {
      val img = indexedFixture()
      val expected = img.argbints().map { (it and 0xFF000000.toInt()) or (it.inv() and 0x00FFFFFF) }
      val out = img.invert()
      out.shouldBePromotedToArgb()
      out.argbList() shouldBe expected
   }

   test("gamma on a custom-palette indexed image matches the ARGB result") {
      val img = indexedFixture()
      val out = img.gamma(2.2)
      out.shouldBePromotedToArgb()
      out.argbList() shouldBe img.copy(BufferedImage.TYPE_INT_ARGB).gamma(2.2).argbList()
      // and it actually changed the colours
      (out.argbList() == img.argbList()) shouldBe false
   }

   test("contrast on a custom-palette indexed image matches the ARGB result") {
      val img = indexedFixture()
      val out = img.contrast(2.0)
      out.shouldBePromotedToArgb()
      out.argbList() shouldBe img.copy(BufferedImage.TYPE_INT_ARGB).contrast(2.0).argbList()
   }

   test("map to a constant colour on a custom-palette indexed image returns that colour") {
      val img = indexedFixture()
      val out = img.map { Color.GREEN }
      out.shouldBePromotedToArgb()
      out.argbList().distinct() shouldBe listOf(Color.GREEN.rgb)
   }

   test("brightness on a custom-palette indexed image scales each channel") {
      val img = indexedFixture()
      val out = img.brightness(1.5)
      out.shouldBePromotedToArgb()
      // truncate(1.5 * channel), alpha untouched
      val expected = img.argbints().map { p ->
         val r = minOf(255, (1.5 * ((p shr 16) and 0xFF)).toInt())
         val g = minOf(255, (1.5 * ((p shr 8) and 0xFF)).toInt())
         val b = minOf(255, (1.5 * (p and 0xFF)).toInt())
         (p and 0xFF000000.toInt()) or (r shl 16) or (g shl 8) or b
      }
      out.argbList() shouldBe expected
   }

   test("removeTransparency on a custom-palette indexed image with a transparent entry fills with the given colour") {
      val img = indexedFixture(transparentIndex = 3)
      val out = img.removeTransparency(Color.WHITE)
      out.shouldBePromotedToArgb()
      out.argbList() shouldBe img.copy(BufferedImage.TYPE_INT_ARGB).removeTransparency(Color.WHITE).argbList()
      // the transparent pixels are now white, which the source palette cannot hold
      out.argbList().count { it == Color.WHITE.rgb } shouldBe img.argbints().count { (it ushr 24) == 0 }
   }

   test("removeTransparency on an opaque custom-palette indexed image is an identity on the colours") {
      val img = indexedFixture()
      img.removeTransparency(Color.WHITE).argbList() shouldBe img.argbList()
   }

   test("filter writing an off-palette colour on a custom-palette indexed image keeps that colour") {
      val img = indexedFixture()
      val out = img.filter(Filter { image -> image.awt().setRGB(0, 0, Color.GREEN.rgb) })
      out.shouldBePromotedToArgb()
      out.pixel(0, 0).toARGBInt() shouldBe Color.GREEN.rgb
      out.argbList().drop(1) shouldBe img.argbList().drop(1)
   }

   test("colour transforms leave the palette-based source untouched") {
      val img = indexedFixture()
      val before = img.argbList()
      img.toGrayscale(); img.invert(); img.gamma(2.2); img.contrast(2.0); img.map { Color.GREEN }
      img.brightness(1.5); img.removeTransparency(Color.WHITE)
      img.filter(Filter { image -> image.awt().setRGB(0, 0, Color.GREEN.rgb) })
      img.argbList() shouldBe before
      img.awt().type shouldBe BufferedImage.TYPE_BYTE_INDEXED
   }

   test("loaded GIF: toGrayscale yields only gray pixels, equal to the ARGB result") {
      val src = gif()
      src.awt().type shouldBe BufferedImage.TYPE_BYTE_INDEXED
      val out = src.toGrayscale()
      out.argbList().count { !isGray(it) } shouldBe 0
      out.argbList() shouldBe src.copy(BufferedImage.TYPE_INT_ARGB).toGrayscale().argbList()
   }

   test("loaded GIF: invert matches the ARGB result") {
      val src = gif()
      src.invert().argbList() shouldBe src.copy(BufferedImage.TYPE_INT_ARGB).invert().argbList()
   }

   test("loaded GIF: gamma matches the ARGB result") {
      val src = gif()
      src.gamma(2.2).argbList() shouldBe src.copy(BufferedImage.TYPE_INT_ARGB).gamma(2.2).argbList()
   }

   test("loaded GIF: contrast matches the ARGB result") {
      val src = gif()
      src.contrast(1.5).argbList() shouldBe src.copy(BufferedImage.TYPE_INT_ARGB).contrast(1.5).argbList()
   }
})
