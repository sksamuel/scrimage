package com.sksamuel.scrimage.core.invariants

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.ScaleMethod
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage

/**
 * Property-style invariants for operations that should be no-ops when
 * called with arguments matching the source image:
 *
 *   image.subimage(0, 0, W, H)      pixel-equals  image
 *   image.takeLeft(W)               pixel-equals  image
 *   image.takeRight(W)              pixel-equals  image
 *   image.takeTop(H)                pixel-equals  image
 *   image.takeBottom(H)             pixel-equals  image
 *   image.bound(W * 2, H * 2)       reference-equals this  (no upscale)
 *   image.scaleTo(W, H, ANY_METHOD) reference-equals this  (W/H unchanged)
 *
 * The bound and scaleTo identities have explicit `if dims match return this`
 * short-circuits in the implementation; the rest go through the full
 * code path and test that the full-extent crop is faithful.
 */
class IdentityOpInvariantTest : FunSpec({

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
      Triple(8, 1, BufferedImage.TYPE_INT_ARGB),
      Triple(1, 8, BufferedImage.TYPE_INT_ARGB),
      Triple(7, 13, BufferedImage.TYPE_INT_ARGB),
      Triple(7, 13, BufferedImage.TYPE_INT_RGB),
      Triple(7, 13, BufferedImage.TYPE_4BYTE_ABGR),
      Triple(32, 24, BufferedImage.TYPE_INT_ARGB),
   )

   test("subimage(0, 0, W, H) is pixel-equal to the source") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         pixelsEqual(img.subimage(0, 0, w, h), img).shouldBeTrue()
      }
   }

   test("takeLeft(W) is pixel-equal to the source") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         pixelsEqual(img.takeLeft(w), img).shouldBeTrue()
      }
   }

   test("takeRight(W) is pixel-equal to the source") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         pixelsEqual(img.takeRight(w), img).shouldBeTrue()
      }
   }

   test("takeTop(H) is pixel-equal to the source") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         pixelsEqual(img.takeTop(h), img).shouldBeTrue()
      }
   }

   test("takeBottom(H) is pixel-equal to the source") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         pixelsEqual(img.takeBottom(h), img).shouldBeTrue()
      }
   }

   test("bound(2W, 2H) returns the same instance (no upscale)") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         (img.bound(w * 2, h * 2) === img) shouldBe true
      }
   }

   test("scaleTo(W, H, method) returns the same instance for every method") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         for (method in ScaleMethod.values()) {
            (img.scaleTo(w, h, method) === img) shouldBe true
         }
      }
   }
})
