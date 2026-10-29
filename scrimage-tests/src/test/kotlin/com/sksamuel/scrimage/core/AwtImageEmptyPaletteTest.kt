package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage
import java.awt.image.IndexColorModel

/**
 * Regression test for AwtImage.empty() on palette-based (IndexColorModel) images.
 *
 * empty() created its target with new BufferedImage(w, h, TYPE_BYTE_INDEXED / TYPE_BYTE_BINARY),
 * which gets Java's default palette rather than the source's, so the source's own colours
 * written to it with setRGB were snapped to the nearest default-palette entry.
 */
class AwtImageEmptyPaletteTest : FunSpec({

   // off-cube colours: none of these are in Java's default palettes
   val r = byteArrayOf(123, 10, 200.toByte(), 77)
   val g = byteArrayOf(45, 200.toByte(), 100, 77)
   val b = byteArrayOf(67, 30, 250.toByte(), 77)
   val colours = (0 until 4).map { i ->
      (0xFF shl 24) or ((r[i].toInt() and 255) shl 16) or ((g[i].toInt() and 255) shl 8) or (b[i].toInt() and 255)
   }

   fun ImmutableImage.roundTrips(): List<String> {
      val empty = empty().awt()
      colours.forEachIndexed { i, c -> empty.setRGB(i, 0, c) }
      return (0 until 4).map { "%08x".format(empty.getRGB(it, 0)) }
   }

   test("empty() of a custom-palette TYPE_BYTE_INDEXED image can store the source palette's colours") {
      val icm = IndexColorModel(8, 4, r, g, b)
      val img = ImmutableImage.wrapAwt(BufferedImage(icm, icm.createCompatibleWritableRaster(4, 4), false, null))
      img.empty().awt().type shouldBe BufferedImage.TYPE_BYTE_INDEXED
      img.roundTrips() shouldBe colours.map { "%08x".format(it) }
   }

   test("empty() of a custom-palette 2-bit TYPE_BYTE_BINARY image can store the source palette's colours") {
      val icm = IndexColorModel(2, 4, r, g, b)
      val img = ImmutableImage.wrapAwt(BufferedImage(icm, icm.createCompatibleWritableRaster(4, 4), false, null))
      img.type shouldBe BufferedImage.TYPE_BYTE_BINARY
      img.empty().awt().type shouldBe BufferedImage.TYPE_BYTE_BINARY
      img.roundTrips() shouldBe colours.map { "%08x".format(it) }
   }

   test("empty() keeps dimensions and type for non-palette images") {
      val img = ImmutableImage.create(5, 3, BufferedImage.TYPE_3BYTE_BGR)
      val empty = img.empty()
      listOf(empty.width, empty.height, empty.awt().type) shouldBe listOf(5, 3, BufferedImage.TYPE_3BYTE_BGR)
   }
})
