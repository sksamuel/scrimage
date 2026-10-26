package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Regression tests for two LensBlurFilter defects visible on a uniform opaque image, which a
 * normalised blur kernel must leave unchanged:
 *
 * 1. The inverse FFT results (e.g. 254.9997) were truncated with a plain int cast, so nearly
 *    every channel, including alpha, came out one level too dark (0xfec76331 for 0xffc86432).
 * 2. When padding a tile past the image edge, rows at exactly imageY == height and columns at
 *    exactly imageX == width were not treated as off-image (the test was `>` instead of `>=`),
 *    so an unfilled scratch row/column was blended into the bottom and right borders, darkening
 *    them and reducing their alpha.
 */
class LensBlurFilterUniformTest : FunSpec({

   val colour = Color(200, 100, 50)
   val expected = 0xffc86432.toInt()

   fun hex(i: Int) = String.format("%08x", i)

   test("lens blur of a uniform opaque image returns exactly the same colour everywhere") {
      val out = ImmutableImage.filled(120, 90, colour).filter(LensBlurFilter())
      out.argbints().toSet().map { hex(it) } shouldBe setOf(hex(expected))
   }

   test("bottom-right corner, bottom row and right column of a uniform image are untouched") {
      val out = ImmutableImage.filled(120, 90, colour).filter(LensBlurFilter())
      hex(out.pixel(119, 89).toARGBInt()) shouldBe hex(expected)
      hex(out.pixel(60, 89).toARGBInt()) shouldBe hex(expected)
      hex(out.pixel(119, 45).toARGBInt()) shouldBe hex(expected)
      hex(out.pixel(0, 0).toARGBInt()) shouldBe hex(expected)
   }

   test("uniform images of other shapes also stay uniform") {
      for ((w, h) in listOf(40 to 40, 60 to 20, 20 to 60, 300 to 200)) {
         val out = ImmutableImage.filled(w, h, colour).filter(LensBlurFilter())
         out.argbints().toSet().map { hex(it) } shouldBe setOf(hex(expected))
      }
   }

   test("a uniform semi-transparent image keeps its alpha") {
      val out = ImmutableImage.filled(50, 40, Color(200, 100, 50, 128)).filter(LensBlurFilter())
      out.argbints().map { it ushr 24 }.toSet() shouldBe setOf(128)
   }
})
