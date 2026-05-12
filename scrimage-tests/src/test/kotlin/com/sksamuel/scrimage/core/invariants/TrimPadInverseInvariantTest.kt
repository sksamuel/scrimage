package com.sksamuel.scrimage.core.invariants

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.awt.image.BufferedImage

/**
 * Property-style invariants for trim and pad as mutual inverses:
 *
 *   image.padWith(l, t, r, b, bg).trim(l, t, r, b)  pixel-equals  image
 *   trim and pad both update width / height predictably
 *
 * `padWith → trim` must be a faithful round-trip because trim is just an
 * exact pixel copy via subimage and padWith composes the original image
 * over a fresh canvas at offset (l, t). The round-trip is what callers
 * implicitly rely on when they pad a working area, do something at the
 * edges, and then crop back to the original size.
 *
 * Sweeps a representative cross product of source dimensions, padding
 * amounts (including asymmetric and one-sided), and image types.
 */
class TrimPadInverseInvariantTest : FunSpec({

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

   data class Pad(val left: Int, val top: Int, val right: Int, val bottom: Int)

   val dimensions = listOf(
      4 to 4,
      7 to 13,
      32 to 16,
   )

   val pads = listOf(
      Pad(0, 0, 0, 0),     // no-op padding
      Pad(1, 1, 1, 1),     // symmetric small
      Pad(5, 5, 5, 5),     // symmetric larger
      Pad(3, 0, 0, 0),     // left only
      Pad(0, 4, 0, 0),     // top only
      Pad(0, 0, 6, 0),     // right only
      Pad(0, 0, 0, 7),     // bottom only
      Pad(2, 4, 6, 8),     // fully asymmetric
   )

   val types = listOf(
      BufferedImage.TYPE_INT_ARGB,
      BufferedImage.TYPE_INT_RGB,
      BufferedImage.TYPE_4BYTE_ABGR,
   )

   test("padWith then trim recovers the original pixel-for-pixel") {
      for ((w, h) in dimensions) for (p in pads) for (t in types) {
         val img = seed(w, h, t)
         val padded = img.padWith(p.left, p.top, p.right, p.bottom, Color.MAGENTA)
         val recovered = padded.trim(p.left, p.top, p.right, p.bottom)
         pixelsEqual(recovered, img).shouldBeTrue()
      }
   }

   test("padWith updates dimensions predictably") {
      for ((w, h) in dimensions) for (p in pads) {
         val img = seed(w, h, BufferedImage.TYPE_INT_ARGB)
         val padded = img.padWith(p.left, p.top, p.right, p.bottom, Color.MAGENTA)
         padded.width shouldBe w + p.left + p.right
         padded.height shouldBe h + p.top + p.bottom
      }
   }

   test("trim updates dimensions predictably") {
      // Need source big enough to absorb every trim amount. Skip the
      // edge case where trim totals would meet/exceed the source — that
      // path is covered by TrimDegenerateInputTest.
      for ((w, h) in listOf(40 to 40, 60 to 80)) for (p in pads) {
         val img = seed(w, h, BufferedImage.TYPE_INT_ARGB)
         val trimmed = img.trim(p.left, p.top, p.right, p.bottom)
         trimmed.width shouldBe w - p.left - p.right
         trimmed.height shouldBe h - p.top - p.bottom
      }
   }
})
