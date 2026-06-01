package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

/**
 * ImmutableImage.create(w, h, pixels[, type]) used a bare `assert` to check
 * that the pixel array length matched w*h. Assertions are disabled by default,
 * so a mismatch would either throw an opaque ArrayIndexOutOfBoundsException
 * (array too short) or silently drop trailing pixels (array too long).
 * It now validates explicitly with a clear IllegalArgumentException.
 */
class CreateFromPixelsValidationTest : FunSpec({

   fun pixelsOfLength(n: Int): Array<Pixel> =
      Array(n) { Pixel(0, 0, 0xFF000000.toInt()) }

   test("create succeeds when length matches w*h") {
      val img = ImmutableImage.create(2, 3, pixelsOfLength(6))
      img.width shouldBe 2
      img.height shouldBe 3
   }

   test("create rejects a too-short pixel array with a clear message") {
      val e = shouldThrow<IllegalArgumentException> {
         ImmutableImage.create(2, 3, pixelsOfLength(5))
      }
      e.message shouldContain "5"
      e.message shouldContain "2x3"
   }

   test("create rejects a too-long pixel array instead of silently dropping pixels") {
      shouldThrow<IllegalArgumentException> {
         ImmutableImage.create(2, 3, pixelsOfLength(7))
      }
   }
})
