package com.sksamuel.scrimage.core.invariants

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage

/**
 * Property-style invariants for the bulk-ARGB accessors on AwtImage.
 *
 *   argbints()  ==  awt().getRGB(0, 0, w, h, null, 0, w)
 *   argbints()[i]  ==  pixels(0, 0, w, h)[i].toARGBInt()
 *   argb()[i]   ==  [pixel.alpha, pixel.red, pixel.green, pixel.blue]
 *                   for the pixel at flat index i
 *   rgb()[i]    ==  [pixel.red, pixel.green, pixel.blue]
 *   colors()[i].toARGBInt()  ==  argbints()[i]
 *
 * These five views (argbints, argb, rgb, colors, pixels) all read the
 * same buffer through different code paths. A divergence — wrong
 * channel order in argb()/rgb(), an off-by-one between the bulk and
 * pixel views, an alpha-stripping bug in any one path — would
 * surface here as a contract violation.
 */
class ArgbAccessorConsistencyInvariantTest : FunSpec({

   fun seed(width: Int, height: Int, type: Int): ImmutableImage {
      val buf = BufferedImage(width, height, type)
      for (y in 0 until height) for (x in 0 until width) {
         val r = (x * 11) and 0xFF
         val g = (y * 13) and 0xFF
         val b = ((x + y) * 7) and 0xFF
         buf.setRGB(x, y, (0xFF shl 24) or (r shl 16) or (g shl 8) or b)
      }
      return ImmutableImage.wrapAwt(buf)
   }

   val cases = listOf(
      Triple(1, 1, BufferedImage.TYPE_INT_ARGB),
      Triple(8, 1, BufferedImage.TYPE_INT_ARGB),
      Triple(1, 8, BufferedImage.TYPE_INT_ARGB),
      Triple(7, 13, BufferedImage.TYPE_INT_ARGB),
      Triple(7, 13, BufferedImage.TYPE_INT_RGB),
      Triple(7, 13, BufferedImage.TYPE_4BYTE_ABGR),
      Triple(32, 16, BufferedImage.TYPE_INT_ARGB),
   )

   test("argbints() matches a fresh awt.getRGB bulk fetch") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         val direct = img.awt().getRGB(0, 0, w, h, null, 0, w)
         val bulk = img.argbints()
         bulk.contentEquals(direct) shouldBe true
      }
   }

   test("argbints()[i] equals pixels(0, 0, w, h)[i].toARGBInt()") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         val ints = img.argbints()
         val pix = img.pixels(0, 0, w, h)
         ints.size shouldBe pix.size
         for (i in ints.indices) {
            ints[i] shouldBe pix[i].toARGBInt()
         }
      }
   }

   test("argb()[i] returns [alpha, red, green, blue] for the pixel at flat index i") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         val argb = img.argb()
         val pix = img.pixels(0, 0, w, h)
         argb.size shouldBe pix.size
         for (i in argb.indices) {
            val row = argb[i]
            row.size shouldBe 4
            row[0] shouldBe pix[i].alpha()
            row[1] shouldBe pix[i].red()
            row[2] shouldBe pix[i].green()
            row[3] shouldBe pix[i].blue()
         }
      }
   }

   test("rgb()[i] returns [red, green, blue] for the pixel at flat index i") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         val rgb = img.rgb()
         val pix = img.pixels(0, 0, w, h)
         rgb.size shouldBe pix.size
         for (i in rgb.indices) {
            val row = rgb[i]
            row.size shouldBe 3
            row[0] shouldBe pix[i].red()
            row[1] shouldBe pix[i].green()
            row[2] shouldBe pix[i].blue()
         }
      }
   }

   test("colors()[i].toARGBInt() equals argbints()[i]") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         val colours = img.colors()
         val ints = img.argbints()
         colours.size shouldBe ints.size
         for (i in colours.indices) {
            colours[i].toARGBInt() shouldBe ints[i]
         }
      }
   }
})
