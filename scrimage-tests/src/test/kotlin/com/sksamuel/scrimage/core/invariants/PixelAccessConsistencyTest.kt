package com.sksamuel.scrimage.core.invariants

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage

/**
 * Property-style tests asserting that the eight pixel-access entry
 * points on AwtImage all agree on the colour at any given (x, y).
 *
 *   pixel(x, y).argb
 *   pixels()[y * width + x].argb
 *   pixels(x, y, 1, 1)[0].argb
 *   rows()[y][x].argb
 *   row(y)[x].argb
 *   col(x)[y].argb
 *   iterator()'s k-th .argb (k = y * width + x)
 *   forEach's accumulated argb at the same position
 *
 * If any one path diverges (a getRGB / setRGB asymmetry, an off-by-one
 * in iterator's hasNext, a row/col index swap, a TYPE_INT_RGB alpha
 * stripping bug — like the one PR earlier in this series fixed) this
 * surfaces immediately.
 */
class PixelAccessConsistencyTest : FunSpec({

   fun seed(width: Int, height: Int, type: Int): ImmutableImage {
      val buf = BufferedImage(width, height, type)
      for (y in 0 until height) for (x in 0 until width) {
         val r = (x * 37) and 0xFF
         val g = (y * 41) and 0xFF
         val b = ((x xor y) * 23) and 0xFF
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
      // Byte-backed buffers exercise the bulk-getRGB fallback in pixels()
      // rather than the DataBufferInt fast path.
      Triple(7, 13, BufferedImage.TYPE_4BYTE_ABGR),
      Triple(7, 13, BufferedImage.TYPE_3BYTE_BGR),
      Triple(7, 13, BufferedImage.TYPE_BYTE_GRAY),
      Triple(32, 16, BufferedImage.TYPE_INT_ARGB),
   )

   test("pixel(x,y) and pixels()[index] agree at every position") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         val flat = img.pixels()
         for (y in 0 until h) for (x in 0 until w) {
            val viaXY = img.pixel(x, y).argb
            val viaIndex = flat[y * w + x].argb
            viaXY shouldBe viaIndex
            flat[y * w + x].x shouldBe x
            flat[y * w + x].y shouldBe y
         }
      }
   }

   test("pixel(x,y) and rows()[y][x] agree at every position") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         val rows = img.rows()
         for (y in 0 until h) for (x in 0 until w) {
            img.pixel(x, y).argb shouldBe rows[y][x].argb
         }
      }
   }

   test("pixel(x,y) and row(y)[x] agree at every position") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         for (y in 0 until h) {
            val r = img.row(y)
            for (x in 0 until w) {
               img.pixel(x, y).argb shouldBe r[x].argb
            }
         }
      }
   }

   test("pixel(x,y) and col(x)[y] agree at every position") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         for (x in 0 until w) {
            val c = img.col(x)
            for (y in 0 until h) {
               img.pixel(x, y).argb shouldBe c[y].argb
            }
         }
      }
   }

   test("pixel(x,y) and pixels(x,y,1,1)[0] agree at every position") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         for (y in 0 until h) for (x in 0 until w) {
            img.pixel(x, y).argb shouldBe img.pixels(x, y, 1, 1)[0].argb
         }
      }
   }

   test("patch(x,y,w,h) agrees with the corresponding region of pixels()") {
      // patch() now delegates to the region read pixels(x, y, w, h) instead of
      // materialising every pixel and slicing; it must still return the same
      // values, in the same row-major order, with the same absolute coordinates,
      // as the equivalent region of the full pixels() array — for both the
      // DataBufferInt fast path and the bulk-getRGB byte-backed fallback.
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         val flat = img.pixels()
         val px = (w - 1) / 2
         val py = (h - 1) / 2
         val pw = w - px
         val ph = h - py
         val patch = img.patch(px, py, pw, ph)
         patch.size shouldBe pw * ph
         for (dy in 0 until ph) for (dx in 0 until pw) {
            val p = patch[dy * pw + dx]
            val expected = flat[(py + dy) * w + (px + dx)]
            p.x shouldBe expected.x
            p.y shouldBe expected.y
            p.argb shouldBe expected.argb
         }
      }
   }

   test("iterator emits pixels in row-major order matching pixel(x,y)") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         val it = img.iterator()
         var k = 0
         while (it.hasNext()) {
            val p = it.next()
            val expectedX = k % w
            val expectedY = k / w
            p.x shouldBe expectedX
            p.y shouldBe expectedY
            p.argb shouldBe img.pixel(expectedX, expectedY).argb
            k++
         }
         k shouldBe w * h
      }
   }

   test("forEach visits every pixel in row-major order matching pixel(x,y)") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         var k = 0
         img.forEach { p ->
            val expectedX = k % w
            val expectedY = k / w
            p.x shouldBe expectedX
            p.y shouldBe expectedY
            p.argb shouldBe img.pixel(expectedX, expectedY).argb
            k++
         }
         k shouldBe w * h
      }
   }
})
