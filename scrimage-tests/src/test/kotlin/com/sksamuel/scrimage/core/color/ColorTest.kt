package com.sksamuel.scrimage.core.color

import com.sksamuel.scrimage.color.CMYKColor
import com.sksamuel.scrimage.color.Grayscale
import com.sksamuel.scrimage.color.HSLColor
import com.sksamuel.scrimage.color.HSVColor
import com.sksamuel.scrimage.color.RGBColor
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe

class ColorTest : WordSpec({

   "color conversions" should {
      "convert rgb to cmyk correctly" {
         val rgb = RGBColor(1, 2, 3)
         rgb.toCMYK().c shouldBe (0.667f plusOrMinus 0.2f)
         rgb.toCMYK().m shouldBe (0.333f plusOrMinus 0.2f)
         rgb.toCMYK().y shouldBe 0f
         rgb.toCMYK().k shouldBe (0.988f plusOrMinus 0.2f)
      }
      "convert rgb to hsl correctly" {
         val hsl = RGBColor(240, 150, 200).toHSL()
         hsl.hue shouldBe (326.66f plusOrMinus 0.2f)
         hsl.saturation shouldBe (0.75f plusOrMinus 0.2f)
         hsl.lightness shouldBe (0.765f plusOrMinus 0.2f)
      }
      "convert achromatic rgb to hsl correctly" {
         val hsl = RGBColor(50, 50, 50).toHSL()
         hsl.hue shouldBe (0f plusOrMinus 0.02f)
         hsl.saturation shouldBe (0f plusOrMinus 0.021f)
         hsl.lightness shouldBe (0.196f plusOrMinus 0.02f)
      }
      "convert rgb to hsv correctly" {
         val hsl = RGBColor(255, 150, 200).toHSV()
         hsl.hue shouldBe (331.42f plusOrMinus 0.2f)
         hsl.saturation shouldBe (0.4121f plusOrMinus 0.2f)
         hsl.value shouldBe (1f plusOrMinus 0.2f)
      }
      "convert rgb to grayscale correctly" {
         val rgb = RGBColor(100, 100, 100)
         rgb.toGrayscale().gray shouldBe 100
      }
      "convert cmyk to rgb correctly" {
         val rgb = CMYKColor(0.1f, 0.2f, 0.3f, 0.4f).toRGB()
         rgb.red shouldBe 138
         rgb.green shouldBe 122
         rgb.blue shouldBe 107
         rgb.alpha shouldBe 255
      }
      "convert hsl to rgb correctly" {
         val rgb = HSLColor(100f, 0.5f, 0.3f, 1f).toRGB()
         rgb.red shouldBe 64
         rgb.green shouldBe 115
         rgb.blue shouldBe 38
         rgb.alpha shouldBe 255
      }
      "convert hsv to rgb correctly 1" {
         val rgb = HSVColor(150f, 0.2f, 0.3f, 1f).toRGB()
         rgb.red shouldBe 61
         rgb.green shouldBe 77
         rgb.blue shouldBe 69
         rgb.alpha shouldBe 255
      }
      "convert hsv to rgb correctly 2" {
         val rgb = HSVColor(2f, 0.99f, 0.61f, 1f).toRGB()
         rgb.red shouldBe 156
         rgb.green shouldBe 7
         rgb.blue shouldBe 2
         rgb.alpha shouldBe 255
      }
      "convert hsv to rgb correctly 3" {
         val rgb = HSVColor(99f, 0.51f, 0.61f, 1f).toRGB()
         rgb.red shouldBe 104
         rgb.green shouldBe 156
         rgb.blue shouldBe 76
         rgb.alpha shouldBe 255
      }
      "convert hsv to rgb correctly 4" {
         val rgb = HSVColor(99f, 0.51f, 0.62f, 1f).toRGB()
         rgb.red shouldBe 106
         rgb.green shouldBe 158
         rgb.blue shouldBe 77
         rgb.alpha shouldBe 255
      }
      "convert grayscale to rgb correctly" {
         val rgb = Grayscale(100, 128).toRGB()
         rgb.red shouldBe 100
         rgb.green shouldBe 100
         rgb.blue shouldBe 100
         rgb.alpha shouldBe 128
      }
      "be symmetric in rgb" {
         val rgb = RGBColor(1, 2, 3)
         rgb.toCMYK().toRGB() shouldBe rgb
         rgb.toHSL().toRGB() shouldBe rgb
         rgb.toHSV().toRGB() shouldBe rgb
      }
      "be symmetric in hsl" {

         val hsl = HSLColor(300f, 0.3f, 0.3f, 0.4f)
         hsl.toRGB().toHSL().hue  shouldBe (hsl.hue plusOrMinus 0.02f)
         hsl.toRGB().toHSL().saturation shouldBe (hsl.saturation plusOrMinus 0.2f)
         hsl.toRGB().toHSL().lightness shouldBe (hsl.lightness plusOrMinus 0.2f)

         hsl.toHSV().toHSL().hue shouldBe (hsl.hue plusOrMinus 0.2f)
         hsl.toHSV().toHSL().saturation shouldBe (hsl.saturation plusOrMinus 0.2f)
         hsl.toHSV().toHSL().lightness shouldBe (hsl.lightness plusOrMinus 0.2f)

         hsl.toCMYK().toHSL().hue shouldBe (hsl.hue plusOrMinus 0.2f)
         hsl.toCMYK().toHSL().saturation shouldBe (hsl.saturation plusOrMinus 0.2f)
         hsl.toCMYK().toHSL().lightness shouldBe (hsl.lightness plusOrMinus 0.2f)
      }
      "be symmetric in HSV" {

         val hsv = HSVColor(300f, 0.2f, 0.3f, 0.4f)
         hsv.toRGB().toHSV().hue shouldBe (hsv.hue plusOrMinus 0.2f)
         hsv.toRGB().toHSV().saturation shouldBe (hsv.saturation plusOrMinus 0.2f)
         hsv.toRGB().toHSV().value shouldBe (hsv.value plusOrMinus 0.2f)

         hsv.toHSL().toHSV().hue shouldBe (hsv.hue plusOrMinus 0.2f)
         hsv.toHSL().toHSV().saturation shouldBe (hsv.saturation plusOrMinus 0.2f)
         hsv.toHSL().toHSV().value shouldBe (hsv.value plusOrMinus 0.2f)

         hsv.toCMYK().toHSV().hue shouldBe (hsv.hue plusOrMinus 0.2f)
         hsv.toCMYK().toHSV().saturation shouldBe (hsv.saturation plusOrMinus 0.2f)
         hsv.toCMYK().toHSV().value shouldBe (hsv.value plusOrMinus 0.2f)
      }
      "be symmetric in cmyk" {
         val cmyk = CMYKColor(0.1f, 0.2f, 0.3f, 0.4f)
         cmyk.toRGB().toCMYK().c shouldBe (cmyk.c plusOrMinus 0.2f)
         cmyk.toRGB().toCMYK().m shouldBe (cmyk.m plusOrMinus 0.2f)
         cmyk.toRGB().toCMYK().y shouldBe (cmyk.y plusOrMinus 0.2f)
         cmyk.toRGB().toCMYK().k shouldBe (cmyk.k plusOrMinus 0.2f)

         cmyk.toHSV().toCMYK().c shouldBe (cmyk.c plusOrMinus 0.2f)
         cmyk.toHSV().toCMYK().m shouldBe (cmyk.m plusOrMinus 0.2f)
         cmyk.toHSV().toCMYK().y shouldBe (cmyk.y plusOrMinus 0.2f)
         cmyk.toHSV().toCMYK().k shouldBe (cmyk.k plusOrMinus 0.2f)

         cmyk.toHSL().toCMYK().c shouldBe (cmyk.c plusOrMinus 0.2f)
         cmyk.toHSL().toCMYK().m shouldBe (cmyk.m plusOrMinus 0.2f)
         cmyk.toHSL().toCMYK().y shouldBe (cmyk.y plusOrMinus 0.2f)
         cmyk.toHSL().toCMYK().k shouldBe (cmyk.k plusOrMinus 0.2f)
      }
      "be reflexive" {
         val rgb = RGBColor(1, 2, 3)
         rgb.toRGB() shouldBe rgb

         val hsl = HSLColor(0.1f, 0.2f, 0.3f, 0.4f)
         hsl.toHSL() shouldBe hsl

         val hsv = HSVColor(0.1f, 0.2f, 0.3f, 0.4f)
         hsv.toHSV() shouldBe hsv

         val cmyk = CMYKColor(0.1f, 0.2f, 0.3f, 0.4f)
         cmyk.toCMYK() shouldBe cmyk

         val gray = Grayscale(100)
         gray.toGrayscale() shouldBe gray
      }
   }

   // Regression tests: Color.average() was returning premultiplied-alpha RGB values
   // stored as straight-alpha, making partially-transparent blends too dark.
   "Color.average" should {
      "source-over opaque colors returns this color" {
         // Porter-Duff source-over: fully opaque source occludes destination entirely
         val c1 = RGBColor(200, 100, 50, 255)
         val c2 = RGBColor(100, 200, 150, 255)
         val avg = c1.average(c2)
         avg.red shouldBe 200
         avg.green shouldBe 100
         avg.blue shouldBe 50
         avg.alpha shouldBe 255
      }
      "both fully transparent returns transparent black" {
         val c1 = RGBColor(255, 0, 0, 0)
         val c2 = RGBColor(0, 255, 0, 0)
         val avg = c1.average(c2)
         avg shouldBe RGBColor(0, 0, 0, 0)
      }
      "transparent source over opaque destination yields destination" {
         val c1 = RGBColor(255, 0, 0, 0)    // invisible red
         val c2 = RGBColor(0, 0, 255, 255)  // opaque blue
         val avg = c1.average(c2)
         avg.red shouldBe 0
         avg.green shouldBe 0
         avg.blue shouldBe 255
         avg.alpha shouldBe 255
      }
      "opaque source over transparent destination yields source" {
         val c1 = RGBColor(255, 0, 0, 255)  // opaque red
         val c2 = RGBColor(0, 0, 255, 0)    // invisible blue
         val avg = c1.average(c2)
         avg.red shouldBe 255
         avg.green shouldBe 0
         avg.blue shouldBe 0
         avg.alpha shouldBe 255
      }
      "semi-transparent blend normalizes RGB by output alpha" {
         // c1 = semi-transparent red (128 alpha), c2 = semi-transparent blue (128 alpha)
         // a1 = a2 = 128/255 ≈ 0.502
         // aOut = 0.502 + 0.502 * (1 - 0.502) ≈ 0.752  →  a = round(0.752 * 255) = 192
         // r_premult = 255 * 0.502 ≈ 128 — old (buggy) code returned 128
         // r_straight = 128 / 0.752 ≈ 170           — fixed code returns 170
         // b_premult = 255 * 0.502 * 0.498 ≈ 64     — old code returned 64
         // b_straight = 64 / 0.752 ≈ 85             — fixed code returns 85
         val c1 = RGBColor(255, 0, 0, 128)
         val c2 = RGBColor(0, 0, 255, 128)
         val avg = c1.average(c2)
         avg.red shouldBe 170
         avg.green shouldBe 0
         avg.blue shouldBe 85
         avg.alpha shouldBe 192
      }
   }

})
