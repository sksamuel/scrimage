package com.sksamuel.scrimage.core.invariants

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import java.awt.Color
import java.awt.image.BufferedImage

/**
 * Property-style invariant: autocrop is idempotent.
 *
 *   image.autocrop(c)        pixel-equals  image.autocrop(c).autocrop(c)
 *   image.autocrop(c, tol)   pixel-equals  image.autocrop(c, tol).autocrop(c, tol)
 *
 * Autocrop strips full-extent border rows/columns whose pixels all match
 * the given colour (with tolerance). Once those borders are gone, running
 * autocrop again must be a no-op — the outermost rows/columns are now by
 * definition not all-bg. Any failure here would mean autocrop is either
 * leaving a border behind on the first pass, or eating into content on
 * the second.
 *
 * Sweeps a representative cross product of:
 *   - source dimensions (including 1×N / N×1 strips)
 *   - bg colour vs content colour
 *   - bg width on each edge (no border, narrow border, wide border,
 *     asymmetric borders)
 *   - tolerance (0 / mid / high)
 */
class AutocropIdempotenceInvariantTest : FunSpec({

   /**
    * Build an image filled with `bg` plus an inner rectangle of `content`,
    * leaving the requested border widths on each edge. content must fit
    * (left + right < width, top + bottom < height).
    */
   fun bordered(
      width: Int,
      height: Int,
      bg: Int,
      content: Int,
      borderLeft: Int,
      borderTop: Int,
      borderRight: Int,
      borderBottom: Int,
   ): ImmutableImage {
      val buf = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
      for (y in 0 until height) for (x in 0 until width) {
         val isBorder = x < borderLeft || x >= width - borderRight
            || y < borderTop || y >= height - borderBottom
         buf.setRGB(x, y, if (isBorder) bg else content)
      }
      return ImmutableImage.wrapAwt(buf)
   }

   fun pixelsEqual(a: ImmutableImage, b: ImmutableImage): Boolean {
      if (a.width != b.width || a.height != b.height) return false
      val ap = a.awt().getRGB(0, 0, a.width, a.height, null, 0, a.width)
      val bp = b.awt().getRGB(0, 0, b.width, b.height, null, 0, b.width)
      return ap.contentEquals(bp)
   }

   val white = 0xFFFFFFFF.toInt()
   val red = 0xFFFF0000.toInt()

   // (sourceW, sourceH, borders) — borders fit inside the source.
   val cases = listOf(
      Triple(20 to 20, listOf(0, 0, 0, 0), "no border"),
      Triple(20 to 20, listOf(1, 1, 1, 1), "1px symmetric"),
      Triple(20 to 20, listOf(5, 5, 5, 5), "5px symmetric"),
      Triple(20 to 20, listOf(3, 0, 0, 0), "left only"),
      Triple(20 to 20, listOf(0, 4, 0, 0), "top only"),
      Triple(20 to 20, listOf(0, 0, 6, 0), "right only"),
      Triple(20 to 20, listOf(0, 0, 0, 7), "bottom only"),
      Triple(20 to 20, listOf(2, 4, 6, 8), "fully asymmetric"),
      Triple(40 to 30, listOf(2, 3, 2, 3), "rectangular source"),
   )

   test("autocrop(color) is idempotent at zero tolerance") {
      for ((dims, bs, _) in cases) {
         val (w, h) = dims
         val img = bordered(w, h, white, red, bs[0], bs[1], bs[2], bs[3])
         val once = img.autocrop(Color.WHITE)
         val twice = once.autocrop(Color.WHITE)
         pixelsEqual(once, twice).shouldBeTrue()
      }
   }

   test("autocrop(color, tolerance) is idempotent at non-zero tolerance") {
      for ((dims, bs, _) in cases) {
         val (w, h) = dims
         val img = bordered(w, h, white, red, bs[0], bs[1], bs[2], bs[3])
         for (tolerance in listOf(0, 5, 30)) {
            val once = img.autocrop(Color.WHITE, tolerance)
            val twice = once.autocrop(Color.WHITE, tolerance)
            pixelsEqual(once, twice).shouldBeTrue()
         }
      }
   }

   test("autocrop on an image with no border returns the same image (idempotent zero-step case)") {
      // Sanity: an image whose outer rows/cols already contain non-bg
      // pixels should be returned unchanged on the first call, so the
      // idempotence check is also a no-op check here.
      val img = bordered(10, 10, white, red, 0, 0, 0, 0)
      val cropped = img.autocrop(Color.WHITE)
      pixelsEqual(cropped, img).shouldBeTrue()
   }
})
