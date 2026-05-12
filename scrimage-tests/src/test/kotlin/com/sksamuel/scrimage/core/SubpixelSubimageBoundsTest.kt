package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

/**
 * Regression tests for ImmutableImage.subpixelSubimage's boundary checks.
 *
 * The original implementation asserted strict inequality:
 *
 *   assert x + subWidth < width;
 *
 * but the loop reads subpixel(xIndex + x) for xIndex in [0, subWidth),
 * giving a maximum x argument of (subWidth - 1) + x. Subpixel only requires
 * arg < width, so the actual constraint is x + subWidth ≤ width — strict
 * inequality rejected legitimate full-width extractions.
 *
 * The validation was via `assert`, which is disabled by default in production
 * JVMs. Out-of-range arguments then surfaced as a bare
 * ArrayIndexOutOfBoundsException from getRGB deep inside the apply loop.
 * The check is now an upfront IllegalArgumentException.
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

   test("subpixelSubimage with x + subWidth > width throws IllegalArgumentException") {
      val image = ImmutableImage.create(4, 4)
      val ex = shouldThrow<IllegalArgumentException> {
         image.subpixelSubimage(0.0, 0.0, 5, 4)
      }
      ex.message!!.shouldContain("subpixelSubimage region")
      ex.message!!.shouldContain("4x4")
   }

   test("subpixelSubimage with negative x throws IllegalArgumentException") {
      val image = ImmutableImage.create(4, 4)
      val ex = shouldThrow<IllegalArgumentException> {
         image.subpixelSubimage(-0.1, 0.0, 2, 2)
      }
      ex.message!!.shouldContain("x=-0.1")
   }

   test("subpixelSubimage with subimage extending past bottom edge throws") {
      val image = ImmutableImage.create(4, 4)
      val ex = shouldThrow<IllegalArgumentException> {
         image.subpixelSubimage(0.0, 3.0, 2, 2)
      }
      ex.message!!.shouldContain("subHeight=2")
   }
})
