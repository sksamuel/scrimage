package com.sksamuel.scrimage.core.invariants

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import java.awt.image.BufferedImage

/**
 * Property-style invariant: nested subimages compose by adding their
 * offsets.
 *
 *   image.subimage(x1, y1, w1, h1).subimage(x2, y2, w2, h2)
 *     pixel-equals
 *   image.subimage(x1 + x2, y1 + y2, w2, h2)
 *
 * Provided the inner window fits inside the outer (x2 + w2 <= w1,
 * y2 + h2 <= h1) the two routes must produce the same pixels — the
 * inner subimage is just selecting a sub-region that's already a
 * sub-region of the source.
 *
 * Catches any subtle off-by-one or coordinate-frame confusion in the
 * subimage implementation, especially around the boundary cases where
 * the inner window starts at (0, 0) or hits the right/bottom edge of
 * the outer window.
 */
class SubimageCompositionInvariantTest : FunSpec({

   fun seed(width: Int, height: Int): ImmutableImage {
      val buf = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
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

   data class Outer(val x: Int, val y: Int, val w: Int, val h: Int)
   data class Inner(val x: Int, val y: Int, val w: Int, val h: Int)

   // Outer window of a 32×24 source, paired with inner windows that fit
   // inside it. Each inner triggers a different boundary case.
   val outers = listOf(
      Outer(0, 0, 32, 24),     // outer covers the whole source
      Outer(4, 3, 20, 16),     // outer is interior
      Outer(0, 0, 1, 1),       // outer is a single pixel (only inner that fits is also 1×1 at (0,0))
      Outer(10, 5, 8, 8),      // small interior
   )

   /**
    * For each outer, generate inners that fit inside it, including:
    *   - inner == outer (full extent)
    *   - inner at top-left corner of outer
    *   - inner against right edge
    *   - inner against bottom edge
    */
   fun innersFor(outer: Outer): List<Inner> {
      val out = mutableListOf<Inner>()
      // Full extent (cannot be larger).
      out.add(Inner(0, 0, outer.w, outer.h))
      if (outer.w > 1 && outer.h > 1) {
         out.add(Inner(0, 0, 1, 1))                       // top-left 1×1
         out.add(Inner(outer.w - 1, outer.h - 1, 1, 1))    // bottom-right 1×1
         out.add(Inner(0, 0, outer.w - 1, outer.h - 1))    // shrink by 1 each
         if (outer.w > 2 && outer.h > 2) {
            out.add(Inner(1, 1, outer.w - 2, outer.h - 2)) // 1px interior shrink
         }
      }
      return out
   }

   test("subimage composition: nested subimages add their offsets") {
      val img = seed(32, 24)
      for (outer in outers) {
         val outerImg = img.subimage(outer.x, outer.y, outer.w, outer.h)
         for (inner in innersFor(outer)) {
            val composed = outerImg.subimage(inner.x, inner.y, inner.w, inner.h)
            val direct = img.subimage(outer.x + inner.x, outer.y + inner.y, inner.w, inner.h)
            pixelsEqual(composed, direct).shouldBeTrue()
         }
      }
   }
})
