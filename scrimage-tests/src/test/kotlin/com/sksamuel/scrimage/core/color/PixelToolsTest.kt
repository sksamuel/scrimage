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

   // Regression: scale() called rgb() without clamping, so any factor that
   // pushed a channel above 255 (or below 0) was masked with & 0xFF and
   // wrapped — meaning a "brightening" call could silently darken a pixel.
   test("scale clamps rather than wrapping when factor pushes a channel above 255") {
      val pixel = PixelTools.rgb(200, 100, 50)
      val scaled = PixelTools.scale(2.0, pixel)
      // Without clamping, red would have been (400 & 0xFF) = 144, i.e. darker
      // than the input. With clamping it caps at 255.
      PixelTools.red(scaled) shouldBe 255
      PixelTools.green(scaled) shouldBe 200
      PixelTools.blue(scaled) shouldBe 100
   }

   test("scale clamps rather than wrapping when factor is negative") {
      val pixel = PixelTools.rgb(200, 100, 50)
      val scaled = PixelTools.scale(-1.0, pixel)
      PixelTools.red(scaled) shouldBe 0
      PixelTools.green(scaled) shouldBe 0
      PixelTools.blue(scaled) shouldBe 0
   }

   test("scale leaves channels unchanged when factor is exactly 1") {
      val pixel = PixelTools.rgb(123, 45, 67)
      val scaled = PixelTools.scale(1.0, pixel)
      PixelTools.red(scaled) shouldBe 123
      PixelTools.green(scaled) shouldBe 45
      PixelTools.blue(scaled) shouldBe 67
   }

   // Regression: replaceTransparencyWithColor divided the premultiplied SrcOver
   // channel sums by 255 instead of by the composite alpha, and forced the output
   // alpha to 255. With a translucent replacement colour this darkened the result:
   // a fully transparent pixel replaced with 50%-alpha red came back as opaque
   // (128, 0, 0) instead of half-transparent pure red (255, 0, 0, 128).
   test("replaceTransparencyWithColor composites a translucent colour without darkening") {
      val transparent = Pixel(0, 0, 0, 0, 0, 0)
      val result = PixelTools.replaceTransparencyWithColor(transparent, Color(255, 0, 0, 128))
      result.alpha() shouldBe 128
      result.red() shouldBe 255
      result.green() shouldBe 0
      result.blue() shouldBe 0
   }

   test("replaceTransparencyWithColor blends a semi-transparent pixel with a translucent colour using the composite alpha") {
      // white at 50% alpha over red at 50% alpha:
      // aOut = 128 + 128 * 127 / 255 = 191
      val semiWhite = Pixel(0, 0, 255, 255, 255, 128)
      val result = PixelTools.replaceTransparencyWithColor(semiWhite, Color(255, 0, 0, 128))
      result.alpha() shouldBe 191
      // red channel: (255*128 + 255*128*127/255) / 191 = 255
      result.red() shouldBe 255
      // green/blue: 255*128 / 191 = 170
      result.green() shouldBe 170
      result.blue() shouldBe 170
   }

   test("replaceTransparencyWithColor returns fully transparent black when both alphas are zero") {
      val transparent = Pixel(0, 0, 10, 20, 30, 0)
      val result = PixelTools.replaceTransparencyWithColor(transparent, Color(255, 0, 0, 0))
      result.argb shouldBe 0
   }

   test("replaceTransparencyWithColor with an opaque colour behaves as before") {
      // fully transparent pixel -> exactly the replacement colour, opaque
      val transparent = Pixel(0, 0, 0, 0, 0, 0)
      val replaced = PixelTools.replaceTransparencyWithColor(transparent, Color(12, 34, 56, 255))
      replaced.alpha() shouldBe 255
      replaced.red() shouldBe 12
      replaced.green() shouldBe 34
      replaced.blue() shouldBe 56

      // semi-transparent pixel -> the original formula's blend, opaque:
      // channel = (c * a + colorC * (255 - a)) / 255
      val semi = Pixel(0, 0, 200, 100, 50, 128)
      val blended = PixelTools.replaceTransparencyWithColor(semi, Color(10, 20, 30, 255))
      blended.alpha() shouldBe 255
      blended.red() shouldBe (200 * 128 + 10 * 127) / 255
      blended.green() shouldBe (100 * 128 + 20 * 127) / 255
      blended.blue() shouldBe (50 * 128 + 30 * 127) / 255

      // opaque pixel -> unchanged
      val opaque = Pixel(0, 0, 1, 2, 3, 255)
      PixelTools.replaceTransparencyWithColor(opaque, Color(255, 255, 255, 255)).argb shouldBe opaque.argb
   }

})
