package com.sksamuel.scrimage.core.invariants

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage

/**
 * Property-style tests for invariants that any correct rotateLeft / rotateRight
 * implementation must satisfy:
 *
 *   image.rotateRight().rotateLeft()                                   == image
 *   image.rotateLeft().rotateRight()                                   == image
 *   image.rotateRight().rotateRight().rotateRight().rotateRight()      == image
 *   image.rotateLeft().rotateLeft().rotateLeft().rotateLeft()          == image
 *   image.rotateRight().rotateRight()                                  == image.rotateLeft().rotateLeft()
 *
 * After rotating ±90°, width and height swap; after a full revolution they
 * return to the original.
 */
class RotateInvariantTest : FunSpec({

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

   // Asymmetric dimensions force width/height to swap on each ±90 rotation,
   // exercising both bounding-box paths in rotateByRadians. 1×N and N×1
   // catch off-by-one in single-row/column rotation.
   val dimensions = listOf(
      1 to 1,
      1 to 5,
      5 to 1,
      4 to 4,
      7 to 13,
      24 to 16,
   )

   val types = listOf(
      BufferedImage.TYPE_INT_ARGB,
      BufferedImage.TYPE_INT_RGB,
      BufferedImage.TYPE_4BYTE_ABGR,
   )

   test("rotateRight followed by rotateLeft recovers the original image") {
      for ((w, h) in dimensions) for (t in types) {
         val img = seed(w, h, t)
         val out = img.rotateRight().rotateLeft()
         pixelsEqual(out, img).shouldBeTrue()
      }
   }

   test("rotateLeft followed by rotateRight recovers the original image") {
      for ((w, h) in dimensions) for (t in types) {
         val img = seed(w, h, t)
         val out = img.rotateLeft().rotateRight()
         pixelsEqual(out, img).shouldBeTrue()
      }
   }

   test("four rotateRights recover the original image") {
      for ((w, h) in dimensions) for (t in types) {
         val img = seed(w, h, t)
         val out = img.rotateRight().rotateRight().rotateRight().rotateRight()
         pixelsEqual(out, img).shouldBeTrue()
      }
   }

   test("four rotateLefts recover the original image") {
      for ((w, h) in dimensions) for (t in types) {
         val img = seed(w, h, t)
         val out = img.rotateLeft().rotateLeft().rotateLeft().rotateLeft()
         pixelsEqual(out, img).shouldBeTrue()
      }
   }

   test("rotateRight twice equals rotateLeft twice (180° rotation)") {
      for ((w, h) in dimensions) for (t in types) {
         val img = seed(w, h, t)
         val rr = img.rotateRight().rotateRight()
         val ll = img.rotateLeft().rotateLeft()
         pixelsEqual(rr, ll).shouldBeTrue()
      }
   }

   test("rotateRight swaps width and height for a single 90° rotation") {
      for ((w, h) in dimensions) for (t in types) {
         val img = seed(w, h, t)
         val rotated = img.rotateRight()
         rotated.width shouldBe h
         rotated.height shouldBe w
      }
   }
})
