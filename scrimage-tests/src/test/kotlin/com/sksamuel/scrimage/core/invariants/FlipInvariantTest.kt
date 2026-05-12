package com.sksamuel.scrimage.core.invariants

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import java.awt.image.BufferedImage

/**
 * Property-style tests for invariants that any correct flipX/flipY
 * implementation must satisfy.
 *
 *   image.flipX().flipX()  pixel-equals  image
 *   image.flipY().flipY()  pixel-equals  image
 *   image.flipX().flipY()  pixel-equals  image.flipY().flipX()
 *
 * Exercised across a representative cross product of image dimensions
 * (including degenerate 1×N and N×1 strips that catch off-by-one in
 * the underlying AffineTransform setup) and image types.
 */
class FlipInvariantTest : FunSpec({

   // Build a deterministic test image with non-trivial pixel data — every
   // pixel encodes its (x, y) so a reflection bug shows up as a mismatch.
   fun seed(width: Int, height: Int, type: Int): ImmutableImage {
      val buf = BufferedImage(width, height, type)
      for (y in 0 until height) for (x in 0 until width) {
         // Use distinct hue, saturation, value-like packing so adjacent
         // pixels differ in every channel.
         val r = (x * 11) and 0xFF
         val g = (y * 13) and 0xFF
         val b = ((x + y) * 7) and 0xFF
         val a = 0xFF
         buf.setRGB(x, y, (a shl 24) or (r shl 16) or (g shl 8) or b)
      }
      return ImmutableImage.wrapAwt(buf)
   }

   fun pixelsEqual(a: ImmutableImage, b: ImmutableImage): Boolean {
      if (a.width != b.width || a.height != b.height) return false
      val ap = a.awt().getRGB(0, 0, a.width, a.height, null, 0, a.width)
      val bp = b.awt().getRGB(0, 0, b.width, b.height, null, 0, b.width)
      return ap.contentEquals(bp)
   }

   val dimensions = listOf(
      1 to 1,
      1 to 8,
      8 to 1,
      4 to 4,
      7 to 13,    // odd width and height — catches centring assumptions
      32 to 24,
   )

   // Restrict to types that getRGB / setRGB round-trip losslessly.
   // TYPE_BYTE_GRAY would lose colour info so the comparison wouldn't
   // be a clean property check on the original RGB.
   val types = listOf(
      BufferedImage.TYPE_INT_ARGB,
      BufferedImage.TYPE_INT_RGB,
      BufferedImage.TYPE_4BYTE_ABGR,
   )

   test("flipX is its own inverse") {
      for ((w, h) in dimensions) for (t in types) {
         val img = seed(w, h, t)
         val twice = img.flipX().flipX()
         pixelsEqual(twice, img).shouldBeTrue()
      }
   }

   test("flipY is its own inverse") {
      for ((w, h) in dimensions) for (t in types) {
         val img = seed(w, h, t)
         val twice = img.flipY().flipY()
         pixelsEqual(twice, img).shouldBeTrue()
      }
   }

   test("flipX and flipY commute") {
      for ((w, h) in dimensions) for (t in types) {
         val img = seed(w, h, t)
         val xy = img.flipX().flipY()
         val yx = img.flipY().flipX()
         pixelsEqual(xy, yx).shouldBeTrue()
      }
   }
})
