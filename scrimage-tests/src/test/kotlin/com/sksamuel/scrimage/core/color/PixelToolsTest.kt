package com.sksamuel.scrimage.core.color

import com.sksamuel.scrimage.pixels.Pixel
import com.sksamuel.scrimage.pixels.PixelTools
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.awt.Point

class PixelToolsTest : FunSpec({

   val white: Int = 0xFFFFFFFF.toInt()
   val yellow: Int = 0xFFFFFF00.toInt()
   val pink: Int = 0xFFFFAFAF.toInt()

   test("non transparent alpha component") {
      white shouldBe java.awt.Color.WHITE.rgb
      PixelTools.alpha(white) shouldBe 255
   }

   test("transparent alpha component") {
      PixelTools.alpha(0xDD001122.toInt()) shouldBe 221
   }

   test("red component") {
      pink shouldBe java.awt.Color.PINK.rgb
      PixelTools.red(yellow) shouldBe 255
      PixelTools.red(pink) shouldBe 255
   }

   test("blue component") {
      pink shouldBe java.awt.Color.PINK.rgb
      PixelTools.blue(yellow) shouldBe 0
      PixelTools.blue(pink) shouldBe 175
   }

   test("green component") {
      yellow shouldBe java.awt.Color.YELLOW.rgb
      PixelTools.green(yellow) shouldBe 255
      PixelTools.green(pink) shouldBe 175
   }

   test("rgb combination") {
      PixelTools.rgb(255, 0, 255) shouldBe 0xFFFF00FF.toInt()
      PixelTools.rgb(85, 102, 119) shouldBe 0xFF556677.toInt()
   }

   test("coordinate to offset") {
      160 shouldBe PixelTools.coordsToOffset(10, 3, 50)
      10 shouldBe PixelTools.coordsToOffset(10, 0, 50)
      99 shouldBe PixelTools.coordsToOffset(49, 1, 50)
   }

   test("offset to coordinate") {
      PixelTools.offsetToPoint(0, 100) shouldBe Point(0, 0)
      PixelTools.offsetToPoint(100, 100) shouldBe Point(0, 1)
      PixelTools.offsetToPoint(99, 100) shouldBe Point(99, 0)
      PixelTools.offsetToPoint(199, 100) shouldBe Point(99, 1)
      PixelTools.offsetToPoint(101, 100) shouldBe Point(1, 1)
   }

   // Regression: approx() compared packed ARGB ints numerically rather than
   // checking each channel independently. A pixel with an out-of-range green
   // (or blue) could pass if its packed int happened to fall within the
   // numeric range of [minColor.toARGBInt(), maxColor.toARGBInt()].
   test("approx rejects pixel whose green is out of tolerance even when packed int is in range") {
      // ref = (255, 100, 100, 100), tolerance = 10  →  each channel must be in [90, 110]
      // pixel has green=200 which is outside [90,110], but the packed int
      // 0xFF64C864 = 4284794890 falls between 0xFF5A5A5A = 4284111450 (min) and
      // 0xFF6E6E6E = 4285427310 (max), so the old code accepted it as a false positive.
      val ref = Color(100, 100, 100, 255)
      val pixel = Pixel(0, 0, 100, 200, 100, 255)  // green=200, way out of tolerance
      PixelTools.approx(ref, 10, arrayOf(pixel)) shouldBe false
   }

   test("approx accepts pixel with all channels within tolerance") {
      val ref = Color(100, 100, 100, 255)
      val pixel = Pixel(0, 0, 105, 95, 108, 255)
      PixelTools.approx(ref, 10, arrayOf(pixel)) shouldBe true
   }

   test("approx rejects pixel whose red is out of tolerance") {
      val ref = Color(100, 100, 100, 255)
      val pixel = Pixel(0, 0, 50, 100, 100, 255)
      PixelTools.approx(ref, 10, arrayOf(pixel)) shouldBe false
   }

})
