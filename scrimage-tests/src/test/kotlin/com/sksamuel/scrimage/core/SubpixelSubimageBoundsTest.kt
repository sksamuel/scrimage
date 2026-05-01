package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Regression test for ImmutableImage.subpixelSubimage's boundary
 * assertions. The previous implementation asserted strict inequality:
 *
 *   assert x + subWidth < width;
 *
 * but the loop reads subpixel(xIndex + x) for xIndex in [0, subWidth),
 * giving a maximum x argument of (subWidth - 1) + x. Subpixel only
 * requires arg < width, so the actual constraint is x + subWidth ≤ width
 * — the strict inequality rejected legitimate full-width extractions.
 *
 * Concrete pre-fix: subpixelSubimage(0.0, 0.0, width, height) on any
 * image throws AssertionError when run under -ea (Gradle's default).
 */
class SubpixelSubimageBoundsTest : FunSpec({

   test("subpixelSubimage with x + subWidth == width succeeds") {
      val pixels = Array(16) { i -> Pixel(i % 4, i / 4, i, 0, 0, 255) }
      val image = ImmutableImage.create(4, 4, pixels)
      // Full width / full height — boundary case.
      val sub = image.subpixelSubimage(0.0, 0.0, 4, 4)
      sub.width shouldBe 4
      sub.height shouldBe 4
   }

   test("subpixelSubimage with x + subWidth == width and offset start succeeds") {
      val pixels = Array(16) { i -> Pixel(i % 4, i / 4, i, 0, 0, 255) }
      val image = ImmutableImage.create(4, 4, pixels)
      // x = 1, subWidth = 3, width = 4 — boundary on the right edge
      val sub = image.subpixelSubimage(1.0, 1.0, 3, 3)
      sub.width shouldBe 3
      sub.height shouldBe 3
   }

   test("subpixelSubimage with x + subWidth > width still asserts") {
      val image = ImmutableImage.create(4, 4)
      shouldThrow<AssertionError> {
         image.subpixelSubimage(0.0, 0.0, 5, 4)
      }
   }

   test("subpixelSubimage with negative x still asserts") {
      val image = ImmutableImage.create(4, 4)
      shouldThrow<AssertionError> {
         image.subpixelSubimage(-0.1, 0.0, 2, 2)
      }
   }
})
