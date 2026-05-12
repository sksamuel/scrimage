package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.RGBColor
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

/**
 * Regression test for AlphaMaskFilter accepting differently-sized
 * masks without checking. The previous code did:
 *
 *     int[] maskPixels = mask.awt().getRGB(0, 0, w, h, null, 0, w);
 *
 * with `w` / `h` taken from the *image*, not the mask. If the mask was
 * smaller, BufferedImage.getRGB threw ArrayIndexOutOfBoundsException
 * deep in the JDK rendering code; if larger, only the top-left corner
 * was sampled and the bottom/right of the mask was silently ignored.
 *
 * The fix throws an IllegalArgumentException up front with a clear
 * message naming both dimensions.
 */
class AlphaMaskFilterTest : FunSpec({

   test("equal-sized mask applies channel correctly (sanity check on the fix's happy path)") {
      val image = ImmutableImage.create(2, 2)
      image.setColor(0, 0, RGBColor(255, 0, 0, 255))
      image.setColor(1, 0, RGBColor(0, 255, 0, 255))
      image.setColor(0, 1, RGBColor(0, 0, 255, 255))
      image.setColor(1, 1, RGBColor(255, 255, 255, 255))

      val mask = ImmutableImage.create(2, 2)
      mask.setColor(0, 0, RGBColor(255, 255, 255, 255))    // alpha=255 → fully opaque
      mask.setColor(1, 0, RGBColor(0, 0, 0, 128))           // alpha=128 → half opaque
      mask.setColor(0, 1, RGBColor(0, 0, 0, 0))             // alpha=0 → fully transparent
      mask.setColor(1, 1, RGBColor(0, 0, 0, 64))            // alpha=64

      AlphaMaskFilter(mask, 0).apply(image)

      image.pixel(0, 0).alpha() shouldBe 255
      image.pixel(1, 0).alpha() shouldBe 128
      image.pixel(0, 1).alpha() shouldBe 0
      image.pixel(1, 1).alpha() shouldBe 64
   }

   test("smaller mask throws IllegalArgumentException with descriptive message") {
      val image = ImmutableImage.create(10, 10)
      val mask = ImmutableImage.create(5, 5)
      val ex = shouldThrow<IllegalArgumentException> {
         AlphaMaskFilter(mask).apply(image)
      }
      ex.message shouldContain "5x5"
      ex.message shouldContain "10x10"
   }

   test("larger mask throws IllegalArgumentException with descriptive message") {
      val image = ImmutableImage.create(5, 5)
      val mask = ImmutableImage.create(10, 10)
      val ex = shouldThrow<IllegalArgumentException> {
         AlphaMaskFilter(mask).apply(image)
      }
      ex.message shouldContain "10x10"
      ex.message shouldContain "5x5"
   }

   test("mask with mismatched width throws") {
      val image = ImmutableImage.create(8, 8)
      val mask = ImmutableImage.create(7, 8)
      shouldThrow<IllegalArgumentException> {
         AlphaMaskFilter(mask).apply(image)
      }
   }

   test("mask with mismatched height throws") {
      val image = ImmutableImage.create(8, 8)
      val mask = ImmutableImage.create(8, 9)
      shouldThrow<IllegalArgumentException> {
         AlphaMaskFilter(mask).apply(image)
      }
   }
})
