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
      val hsl = RGBColor(240, 150, 200).toHSL
      hsl.hue shouldBe 326.66f +- 0.01f
      hsl.saturation shouldBe 0.75f +- 0.01f
      hsl.lightness shouldBe 0.765f +- 0.01f
    }
    "convert rgb to hsv correctly" in {
      val hsl = RGBColor(255, 150, 200).toHSV
      hsl.hue shouldBe 331.42f +- 0.01f
      hsl.saturation shouldBe 0.4121f +- 0.01f
      hsl.value shouldBe 1f +- 0.01f
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
    "be symmetric in rgb" in {
      val rgb = RGBColor(1, 2, 3)
      rgb.toCMYK.toRGB shouldBe rgb
      rgb.toHSL.toRGB shouldBe rgb
      rgb.toHSV.toRGB shouldBe rgb
    }
    "be symmetric in hsl" in {

      val hsl = HSLColor(300f, 0.3f, 0.3f, 0.4f)
      hsl.toRGB.toHSL.hue shouldBe hsl.hue +- 0.1f
      hsl.toRGB.toHSL.saturation shouldBe hsl.saturation +- 0.1f
      hsl.toRGB.toHSL.lightness shouldBe hsl.lightness +- 0.1f

      hsl.toHSV.toHSL.hue shouldBe hsl.hue +- 0.1f
      hsl.toHSV.toHSL.saturation shouldBe hsl.saturation +- 0.1f
      hsl.toHSV.toHSL.lightness shouldBe hsl.lightness +- 0.1f

      hsl.toCMYK.toHSL.hue shouldBe hsl.hue +- 0.1f
      hsl.toCMYK.toHSL.saturation shouldBe hsl.saturation +- 0.1f
      hsl.toCMYK.toHSL.lightness shouldBe hsl.lightness +- 0.1f
    }
    "be symmetric in HSV" in {

      val hsv = HSVColor(300f, 0.2f, 0.3f, 0.4f)
      hsv.toRGB.toHSV.hue shouldBe hsv.hue +- 0.1f
      hsv.toRGB.toHSV.saturation shouldBe hsv.saturation +- 0.1f
      hsv.toRGB.toHSV.value shouldBe hsv.value +- 0.1f

      hsv.toHSL.toHSV.hue shouldBe hsv.hue +- 0.1f
      hsv.toHSL.toHSV.saturation shouldBe hsv.saturation +- 0.1f
      hsv.toHSL.toHSV.value shouldBe hsv.value +- 0.1f

      hsv.toCMYK.toHSV.hue shouldBe hsv.hue +- 0.1f
      hsv.toCMYK.toHSV.saturation shouldBe hsv.saturation +- 0.1f
      hsv.toCMYK.toHSV.value shouldBe hsv.value +- 0.1f
    }
    "be symmetric in cmyk" in {
      val cmyk = CMYKColor(0.1f, 0.2f, 0.3f, 0.4f)
      cmyk.toRGB.toCMYK.c shouldBe cmyk.c +- 0.1f
      cmyk.toRGB.toCMYK.m shouldBe cmyk.m +- 0.1f
      cmyk.toRGB.toCMYK.y shouldBe cmyk.y +- 0.1f
      cmyk.toRGB.toCMYK.k shouldBe cmyk.k +- 0.1f

      cmyk.toHSV.toCMYK.c shouldBe cmyk.c +- 0.1f
      cmyk.toHSV.toCMYK.m shouldBe cmyk.m +- 0.1f
      cmyk.toHSV.toCMYK.y shouldBe cmyk.y +- 0.1f
      cmyk.toHSV.toCMYK.k shouldBe cmyk.k +- 0.1f

      cmyk.toHSL.toCMYK.c shouldBe cmyk.c +- 0.1f
      cmyk.toHSL.toCMYK.m shouldBe cmyk.m +- 0.1f
      cmyk.toHSL.toCMYK.y shouldBe cmyk.y +- 0.1f
      cmyk.toHSL.toCMYK.k shouldBe cmyk.k +- 0.1f
    }
    "be reflexive" in {
      val rgb = RGBColor(1, 2, 3)
      rgb.toRGB shouldBe rgb

      val hsl = HSLColor(0.1f, 0.2f, 0.3f, 0.4f)
      hsl.toHSL shouldBe hsl

      val hsv = HSVColor(0.1f, 0.2f, 0.3f, 0.4f)
      hsv.toHSV shouldBe hsv

      val cmyk = CMYKColor(0.1f, 0.2f, 0.3f, 0.4f)
      cmyk.toCMYK shouldBe cmyk
    }
  }
}
