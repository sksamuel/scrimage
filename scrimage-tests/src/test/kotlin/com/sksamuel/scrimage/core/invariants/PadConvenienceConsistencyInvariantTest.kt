package com.sksamuel.scrimage.core.invariants

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import java.awt.Color
import java.awt.image.BufferedImage

/**
 * Property-style invariants confirming the pad convenience methods
 * produce the same image as the equivalent canonical padWith() call:
 *
 *   image.padLeft(k, c)     pixel-equals  image.padWith(k, 0, 0, 0, c)
 *   image.padRight(k, c)    pixel-equals  image.padWith(0, 0, k, 0, c)
 *   image.padTop(k, c)      pixel-equals  image.padWith(0, k, 0, 0, c)
 *   image.padBottom(k, c)   pixel-equals  image.padWith(0, 0, 0, k, c)
 *   image.pad(s, c)         pixel-equals  image.padWith(s, s, s, s, c)
 *
 * The convenience methods exist so callers don't have to remember the
 * left/top/right/bottom argument order. A typo or transposition in one
 * of the convenience implementations would shift the source by one
 * edge — which would silently produce a wrong-looking image without any
 * exception — so this test catches such regressions.
 */
class PadConvenienceConsistencyInvariantTest : FunSpec({

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

   fun pixelsEqual(a: ImmutableImage, b: ImmutableImage): Boolean {
      if (a.width != b.width || a.height != b.height) return false
      val ap = a.awt().getRGB(0, 0, a.width, a.height, null, 0, a.width)
      val bp = b.awt().getRGB(0, 0, b.width, b.height, null, 0, b.width)
      return ap.contentEquals(bp)
   }

   val cases = listOf(
      Triple(1, 1, BufferedImage.TYPE_INT_ARGB),
      Triple(8, 8, BufferedImage.TYPE_INT_ARGB),
      Triple(7, 13, BufferedImage.TYPE_INT_ARGB),
      Triple(7, 13, BufferedImage.TYPE_INT_RGB),
      Triple(7, 13, BufferedImage.TYPE_4BYTE_ABGR),
      Triple(32, 16, BufferedImage.TYPE_INT_ARGB),
   )

   val ks = listOf(0, 1, 4, 11)

   test("padLeft(k, c) equals padWith(k, 0, 0, 0, c)") {
      for ((w, h, t) in cases) for (k in ks) {
         val img = seed(w, h, t)
         pixelsEqual(img.padLeft(k, Color.MAGENTA), img.padWith(k, 0, 0, 0, Color.MAGENTA)).shouldBeTrue()
      }
   }

   test("padRight(k, c) equals padWith(0, 0, k, 0, c)") {
      for ((w, h, t) in cases) for (k in ks) {
         val img = seed(w, h, t)
         pixelsEqual(img.padRight(k, Color.MAGENTA), img.padWith(0, 0, k, 0, Color.MAGENTA)).shouldBeTrue()
      }
   }

   test("padTop(k, c) equals padWith(0, k, 0, 0, c)") {
      for ((w, h, t) in cases) for (k in ks) {
         val img = seed(w, h, t)
         pixelsEqual(img.padTop(k, Color.MAGENTA), img.padWith(0, k, 0, 0, Color.MAGENTA)).shouldBeTrue()
      }
   }

   test("padBottom(k, c) equals padWith(0, 0, 0, k, c)") {
      for ((w, h, t) in cases) for (k in ks) {
         val img = seed(w, h, t)
         pixelsEqual(img.padBottom(k, Color.MAGENTA), img.padWith(0, 0, 0, k, Color.MAGENTA)).shouldBeTrue()
      }
   }

   test("pad(size, c) equals padWith(size, size, size, size, c)") {
      for ((w, h, t) in cases) for (k in ks) {
         val img = seed(w, h, t)
         pixelsEqual(img.pad(k, Color.MAGENTA), img.padWith(k, k, k, k, Color.MAGENTA)).shouldBeTrue()
      }
   }

   test("padLeft(k) and padLeft(k, Transparent) agree") {
      // The single-arg convenience overloads should be equivalent to the
      // two-arg ones with Transparent — same shape as the rest of the
      // pad family.
      for ((w, h, t) in cases) for (k in ks) {
         val img = seed(w, h, t)
         pixelsEqual(img.padLeft(k), img.padLeft(k, com.sksamuel.scrimage.color.Colors.Transparent.toAWT())).shouldBeTrue()
         pixelsEqual(img.padRight(k), img.padRight(k, com.sksamuel.scrimage.color.Colors.Transparent.toAWT())).shouldBeTrue()
      }
   }
})
