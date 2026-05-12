package com.sksamuel.scrimage.core.invariants

import com.sksamuel.scrimage.AwtImage
import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage

/**
 * Property-style tests for the Java equals/hashCode contract on
 * AwtImage / ImmutableImage:
 *
 *   if a.equals(b) is true then a.hashCode() == b.hashCode()  (mandatory)
 *   reflexive:    a.equals(a) is true
 *   symmetric:    a.equals(b) <=> b.equals(a)
 *   transitive:   a==b && b==c => a==c
 *   self-hash:    same pixel data + same dimensions in two separately-built
 *                 images hash to the same int
 *
 * Different code paths populate the underlying buffer (setRGB vs bulk
 * setRGB vs constructor with type), and equals reads via getRGB while
 * hashCode walks pixels() — so a divergence between those paths would
 * surface here.
 */
class ImageEqualsHashCodeContractTest : FunSpec({

   fun seed(width: Int, height: Int, type: Int, salt: Int = 0): ImmutableImage {
      val buf = BufferedImage(width, height, type)
      for (y in 0 until height) for (x in 0 until width) {
         val r = ((x + salt) * 11) and 0xFF
         val g = ((y + salt) * 13) and 0xFF
         val b = ((x + y + salt) * 7) and 0xFF
         buf.setRGB(x, y, (0xFF shl 24) or (r shl 16) or (g shl 8) or b)
      }
      return ImmutableImage.wrapAwt(buf)
   }

   val cases = listOf(
      Triple(1, 1, BufferedImage.TYPE_INT_ARGB),
      Triple(8, 8, BufferedImage.TYPE_INT_ARGB),
      Triple(7, 13, BufferedImage.TYPE_INT_ARGB),
      Triple(7, 13, BufferedImage.TYPE_INT_RGB),
      Triple(7, 13, BufferedImage.TYPE_4BYTE_ABGR),
      Triple(32, 16, BufferedImage.TYPE_INT_ARGB),
   )

   test("equals is reflexive") {
      for ((w, h, t) in cases) {
         val img: AwtImage = seed(w, h, t)
         (img == img) shouldBe true
      }
   }

   test("two independently-built images with identical pixels are equal") {
      for ((w, h, t) in cases) {
         val a: AwtImage = seed(w, h, t)
         val b: AwtImage = seed(w, h, t)
         (a == b) shouldBe true
         // and equals is symmetric
         (b == a) shouldBe true
      }
   }

   test("equals/hashCode contract: equal images have equal hash codes") {
      for ((w, h, t) in cases) {
         val a: AwtImage = seed(w, h, t)
         val b: AwtImage = seed(w, h, t)
         a.hashCode() shouldBe b.hashCode()
      }
   }

   test("equals is transitive") {
      for ((w, h, t) in cases) {
         val a: AwtImage = seed(w, h, t)
         val b: AwtImage = seed(w, h, t)
         val c: AwtImage = seed(w, h, t)
         (a == b) shouldBe true
         (b == c) shouldBe true
         (a == c) shouldBe true
      }
   }

   test("images with different pixel data are not equal") {
      for ((w, h, t) in cases) {
         val a: AwtImage = seed(w, h, t, salt = 0)
         val b: AwtImage = seed(w, h, t, salt = 1)
         (a == b).shouldBeFalse()
      }
   }

   test("images with different dimensions are not equal") {
      val a: AwtImage = seed(8, 8, BufferedImage.TYPE_INT_ARGB)
      val b: AwtImage = seed(8, 9, BufferedImage.TYPE_INT_ARGB)
      val c: AwtImage = seed(9, 8, BufferedImage.TYPE_INT_ARGB)
      (a == b).shouldBeFalse()
      (a == c).shouldBeFalse()
      (b == c).shouldBeFalse()
   }
})
