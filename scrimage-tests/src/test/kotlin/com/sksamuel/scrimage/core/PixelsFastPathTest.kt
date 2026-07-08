package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.awt.image.BufferedImage

/**
 * Pin-down tests for the DataBufferInt fast path in AwtImage.pixels().
 *
 * The fast path indexes the raster's backing int[] directly, which is only
 * valid when the raster maps 1:1 onto the buffer. A sub-image view created
 * with BufferedImage.getSubimage shares the parent's buffer with a translated
 * origin and the parent's scanline stride, but still reports TYPE_INT_ARGB,
 * so it used to take the fast path and return the parent's full buffer
 * (wrong count, wrong values, wrong coordinates).
 *
 * The fast path also used to throw RuntimeException for other int-packed
 * types (TYPE_INT_BGR, TYPE_INT_ARGB_PRE) even though the getRGB fallback
 * handles them correctly.
 */
class PixelsFastPathTest : FunSpec({

   fun parentWithCoordinateColors(): BufferedImage {
      val parent = BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB)
      for (y in 0 until 8) {
         for (x in 0 until 8) {
            // encode the coordinates in the pixel value so misreads are obvious
            parent.setRGB(x, y, (0xFF shl 24) or (x shl 16) or (y shl 8) or 0x12)
         }
      }
      return parent
   }

   test("pixels() on a wrapAwt'd subimage returns w*h pixels with the view's values and coordinates") {
      val parent = parentWithCoordinateColors()
      val view = ImmutableImage.wrapAwt(parent.getSubimage(2, 2, 4, 4))
      val pixels = view.pixels()
      pixels.size shouldBe 16
      for (p in pixels) {
         // pixel (x,y) of the view is pixel (x+2,y+2) of the parent
         p.argb shouldBe ((0xFF shl 24) or ((p.x + 2) shl 16) or ((p.y + 2) shl 8) or 0x12)
      }
   }

   test("pixels() coordinates on a subimage use the view's width, not the parent's") {
      val parent = parentWithCoordinateColors()
      val view = ImmutableImage.wrapAwt(parent.getSubimage(2, 2, 4, 4))
      val pixels = view.pixels()
      pixels.map { it.x to it.y } shouldBe (0 until 4).flatMap { y -> (0 until 4).map { x -> x to y } }
   }

   test("pixels() works for TYPE_INT_BGR instead of throwing") {
      val image = ImmutableImage.filled(4, 4, Color.RED, BufferedImage.TYPE_INT_BGR)
      val pixels = image.pixels()
      pixels.size shouldBe 16
      pixels.forEach { it.argb shouldBe 0xFFFF0000.toInt() }
   }

   test("pixels() works for TYPE_INT_ARGB_PRE instead of throwing") {
      val image = ImmutableImage.filled(4, 4, Color.RED, BufferedImage.TYPE_INT_ARGB_PRE)
      val pixels = image.pixels()
      pixels.size shouldBe 16
      pixels.forEach { it.argb shouldBe 0xFFFF0000.toInt() }
   }

   test("pixels() fast path still used for a plain TYPE_INT_ARGB image") {
      val image = ImmutableImage.filled(3, 2, Color.GREEN, BufferedImage.TYPE_INT_ARGB)
      val pixels = image.pixels()
      pixels.size shouldBe 6
      pixels.forEach { it.argb shouldBe 0xFF00FF00.toInt() }
   }

   test("patch() on a subimage-backed image returns the correct values") {
      val parent = parentWithCoordinateColors()
      val view = ImmutableImage.wrapAwt(parent.getSubimage(2, 2, 4, 4))
      val patch = view.patch(1, 1, 2, 2)
      patch.size shouldBe 4
      // patch (1,1)-(2,2) of the view maps to (3,3)-(4,4) of the parent
      patch.map { it.argb } shouldBe listOf(
         (0xFF shl 24) or (3 shl 16) or (3 shl 8) or 0x12,
         (0xFF shl 24) or (4 shl 16) or (3 shl 8) or 0x12,
         (0xFF shl 24) or (3 shl 16) or (4 shl 8) or 0x12,
         (0xFF shl 24) or (4 shl 16) or (4 shl 8) or 0x12
      )
   }
})
