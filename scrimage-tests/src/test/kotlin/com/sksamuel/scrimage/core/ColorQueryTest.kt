package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Pin-down tests for the int-comparison fast paths of contains(Color),
 * count(Color), and isFilled(Color).
 */
class ColorQueryTest : FunSpec({

   test("contains returns true when at least one pixel matches the color") {
      val pixels = arrayOf(
         Pixel(0, 0, 255, 0, 0, 255),
         Pixel(1, 0, 0, 255, 0, 255),
         Pixel(0, 1, 0, 0, 255, 255),
         Pixel(1, 1, 255, 255, 255, 255)
      )
      val image = ImmutableImage.create(2, 2, pixels)
      image.contains(Color(0, 255, 0)) shouldBe true
      image.contains(Color.WHITE) shouldBe true
   }

   test("contains returns false when no pixel matches") {
      val pixels = arrayOf(Pixel(0, 0, 255, 0, 0, 255))
      val image = ImmutableImage.create(1, 1, pixels)
      image.contains(Color.GREEN) shouldBe false
   }

   test("contains is alpha-aware (RGBA exact match required)") {
      // pixel is opaque red; checking for transparent red should NOT match
      val pixels = arrayOf(Pixel(0, 0, 255, 0, 0, 255))
      val image = ImmutableImage.create(1, 1, pixels)
      image.contains(Color(255, 0, 0, 128)) shouldBe false
   }

   test("count(Color) returns the number of exact ARGB matches") {
      val pixels = arrayOf(
         Pixel(0, 0, 255, 0, 0, 255),
         Pixel(1, 0, 0, 255, 0, 255),
         Pixel(0, 1, 255, 0, 0, 255),
         Pixel(1, 1, 255, 0, 0, 255)
      )
      val image = ImmutableImage.create(2, 2, pixels)
      image.count(Color.RED) shouldBe 3L
      image.count(Color.GREEN) shouldBe 1L
      image.count(Color.BLUE) shouldBe 0L
   }

   test("isFilled returns true when every pixel matches") {
      val pixels = Array(9) { Pixel(it % 3, it / 3, 100, 200, 50, 255) }
      val image = ImmutableImage.create(3, 3, pixels)
      image.isFilled(Color(100, 200, 50, 255)) shouldBe true
   }

   test("isFilled returns false when one pixel differs") {
      val pixels = Array(9) { i ->
         if (i == 4) Pixel(i % 3, i / 3, 0, 0, 0, 255)
         else Pixel(i % 3, i / 3, 100, 200, 50, 255)
      }
      val image = ImmutableImage.create(3, 3, pixels)
      image.isFilled(Color(100, 200, 50, 255)) shouldBe false
   }
})
