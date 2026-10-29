package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.RGBColor
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

/**
 * Pin-down tests for AwtImage.colours() after the dedup-on-int fast path:
 * dedup is performed on packed ARGB ints first, then RGBColor is allocated
 * only for the surviving distinct values.
 */
class ColoursDistinctTest : FunSpec({

   test("colours returns the set of distinct ARGB values, alpha included") {
      val pixels = arrayOf(
         Pixel(0, 0, 255, 0, 0, 255),     // opaque red
         Pixel(1, 0, 0, 255, 0, 255),     // opaque green
         Pixel(0, 1, 255, 0, 0, 255),     // duplicate opaque red
         Pixel(1, 1, 255, 0, 0, 128)      // semi-transparent red — distinct from opaque
      )
      val image = ImmutableImage.create(2, 2, pixels)
      val colours = image.colours()
      colours shouldContainExactlyInAnyOrder setOf(
         RGBColor(255, 0, 0, 255),
         RGBColor(0, 255, 0, 255),
         RGBColor(255, 0, 0, 128)
      )
   }

   test("colours of a uniform image is a single-element set") {
      val pixels = Array(16) { Pixel(it % 4, it / 4, 100, 200, 50, 255) }
      val image = ImmutableImage.create(4, 4, pixels)
      val colours = image.colours()
      colours.size shouldBe 1
      colours.first() shouldBe RGBColor(100, 200, 50, 255)
   }

   test("colours preserves alpha (transparent black is distinct from opaque black)") {
      val pixels = arrayOf(
         Pixel(0, 0, 0, 0, 0, 255),
         Pixel(1, 0, 0, 0, 0, 0)
      )
      val image = ImmutableImage.create(2, 1, pixels)
      val colours = image.colours()
      colours.size shouldBe 2
      colours shouldContainExactlyInAnyOrder setOf(
         RGBColor(0, 0, 0, 255),
         RGBColor(0, 0, 0, 0)
      )
   }
   test("colours matches a reference distinct set on an image with hundreds of thousands of colours") {
      // enough distinct values to force the internal primitive set to grow several times,
      // with duplicates and the packed value 0 (transparent black) included
      val rnd = java.util.Random(42)
      val w = 700
      val h = 500
      val argb = IntArray(w * h) { if (it % 3 == 0) rnd.nextInt(1000) else rnd.nextInt() }
      argb[17] = 0
      val awt = java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB)
      awt.setRGB(0, 0, w, h, argb, 0, w)
      val expected = argb.toSet().map { RGBColor.fromARGBInt(it) }.toSet()
      val colours = ImmutableImage.wrapAwt(awt).colours()
      colours.size shouldBe expected.size
      colours shouldBe expected
   }
})
