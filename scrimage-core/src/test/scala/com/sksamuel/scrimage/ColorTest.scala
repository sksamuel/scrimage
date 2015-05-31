package com.sksamuel.scrimage

import org.scalatest.{Matchers, WordSpec}

class ColorTest extends WordSpec with Matchers {

  "color conversions" should {
    "convert rgb to cmyk correctly" in {
      val rgb = RGBColor(1, 2, 3)
      rgb.toCMYK.c shouldBe 0.667f +- 0.01f
      rgb.toCMYK.m shouldBe 0.333f +- 0.01f
      rgb.toCMYK.y shouldBe 0
      rgb.toCMYK.k shouldBe 0.988f +- 0.01f
    }
    "convert rgb to hsl correctly" in {
      val hsl = RGBColor(255, 150, 200).toHSL
      hsl.hue shouldBe 331f +- 0.01f
      hsl.saturation shouldBe 100f +- 0.01f
      hsl.lightness shouldBe 0.79f +- 0.01f
    }
    "convert rgb to hsv correctly" in {
      val hsl = RGBColor(255, 150, 200).toHSL
      hsl.hue shouldBe 210f +- 0.01f
      hsl.saturation shouldBe 0.500f +- 0.01f
      hsl.lightness shouldBe 0.784f +- 0.01f
    }
    "convert cmyk to rgb correctly" in {
      val rgb = CMYKColor(0.1f, 0.2f, 0.3f, 0.4f).toRGB
      rgb.red shouldBe 138
      rgb.green shouldBe 122
      rgb.blue shouldBe 107
      rgb.alpha shouldBe 255
    }
    "convert hsl to rgb correctly" in {
      val rgb = HSLColor(100, 0.5f, 0.3f, 1f).toRGB
      rgb.red shouldBe 64
      rgb.green shouldBe 115
      rgb.blue shouldBe 38
      rgb.alpha shouldBe 255
    }
    "convert hsv to rgb correctly" in {
      val rgb = HSVColor(150, 0.2f, 0.3f, 1f).toRGB
      rgb.red shouldBe 61
      rgb.green shouldBe 77
      rgb.blue shouldBe 69
      rgb.alpha shouldBe 255
    }
    "be symmetric" in {
      val rgb = RGBColor(1, 2, 3)
      rgb.toCMYK.toRGB shouldBe rgb
      rgb.toHSL.toRGB shouldBe rgb
      rgb.toHSV.toRGB shouldBe rgb

      val hsl = HSLColor(0.1f, 0.2f, 0.3f, 0.4f)
      hsl.toRGB.toHSL shouldBe hsl
      hsl.toHSV.toHSL shouldBe hsl
      hsl.toCMYK.toHSL shouldBe hsl

      val hsv = HSVColor(0.1f, 0.2f, 0.3f, 0.4f)
      hsv.toRGB.toHSV shouldBe hsv
      hsv.toHSL.toHSV shouldBe hsv
      hsv.toCMYK.toHSV shouldBe hsv

      val cmyk = CMYKColor(0.1f, 0.2f, 0.3f, 0.4f)
      cmyk.toRGB.toCMYK shouldBe cmyk
      cmyk.toHSL.toCMYK shouldBe cmyk
      cmyk.toHSV.toCMYK shouldBe cmyk
    }
    "be reflexive" in {
      val rgb = RGBColor(1, 2, 3)
      rgb.toRGB shouldBe rgb

      val hsl = HSLColor(0.1f, 0.2f, 0.3f, 0.4f)
      hsl.toHSL shouldBe hsl

      val hsv = HSVColor(0.1f, 0.2f, 0.3f, 0.4f)
      hsv.toHSV shouldBe hsl

      val cmyk = CMYKColor(0.1f, 0.2f, 0.3f, 0.4f)
      cmyk.toCMYK shouldBe cmyk
    }
  }
}
