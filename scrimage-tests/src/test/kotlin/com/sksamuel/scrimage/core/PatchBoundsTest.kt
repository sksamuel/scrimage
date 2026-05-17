package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Regression: `AwtImage.patch(x, y, patchWidth, patchHeight)` had no
 * bounds check. The internal helper does a contiguous `System.arraycopy`
 * of `patchWidth` pixels per row from a row-major flat array; if
 * `x + patchWidth > width` the copy silently spans into the next row,
 * returning the wrong pixels rather than failing. If the read goes off
 * the end of the source array the call throws ArrayIndexOutOfBoundsException
 * from inside arraycopy with no useful context.
 */
class PatchBoundsTest : FunSpec({

   // 4x4 image where each pixel's red channel equals its flat index 0..15.
   fun indexedImage(): ImmutableImage {
      val pixels = Array(16) { i -> Pixel(i % 4, i / 4, i, 0, 0, 255) }
      return ImmutableImage.create(4, 4, pixels)
   }

   test("in-bounds patch returns the correct pixels") {
      val image = indexedImage()
      val patch = image.patch(1, 1, 2, 2)
      // (1,1)=5, (2,1)=6, (1,2)=9, (2,2)=10 — row-major
      patch[0].red() shouldBe 5
      patch[1].red() shouldBe 6
      patch[2].red() shouldBe 9
      patch[3].red() shouldBe 10
   }

   test("patch overflowing the right edge throws IllegalArgumentException") {
      val image = indexedImage()
      // x=3, patchWidth=2 → would silently span into the next row
      // (returning pixels [3, 4, 7, 8] = right column of row 0 +
      // left column of row 1).
      shouldThrow<IllegalArgumentException> { image.patch(3, 0, 2, 2) }
   }

   test("patch overflowing the bottom edge throws IllegalArgumentException") {
      val image = indexedImage()
      // y=3, patchHeight=2 → row 4 doesn't exist; pre-fix this threw
      // an opaque ArrayIndexOutOfBoundsException from inside arraycopy.
      shouldThrow<IllegalArgumentException> { image.patch(0, 3, 2, 2) }
   }

   test("patch with negative origin throws IllegalArgumentException") {
      val image = indexedImage()
      shouldThrow<IllegalArgumentException> { image.patch(-1, 0, 2, 2) }
      shouldThrow<IllegalArgumentException> { image.patch(0, -1, 2, 2) }
   }

   test("patch matching the full image is allowed") {
      val image = indexedImage()
      val full = image.patch(0, 0, 4, 4)
      full.size shouldBe 16
      full[15].red() shouldBe 15
   }
})
