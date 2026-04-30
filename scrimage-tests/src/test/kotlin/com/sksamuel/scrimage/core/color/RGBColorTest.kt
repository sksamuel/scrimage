package com.sksamuel.scrimage.core.color

import com.sksamuel.scrimage.color.HSLColor
import com.sksamuel.scrimage.color.HSVColor
import com.sksamuel.scrimage.color.RGBColor
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe
import java.awt.Color

class RGBColorTest : StringSpec({

   "an RGB color convert to hex" {
      "FF00FF" shouldBe RGBColor(255, 0, 255).toHex()
      "FFFFFF" shouldBe RGBColor(255, 255, 255).toHex()
      "EFEFEF" shouldBe RGBColor(239, 239, 239).toHex()
      RGBColor(0, 0, 15).toHex() shouldBe "00000F"
      RGBColor(0, 0, 0).toHex() shouldBe "000000"
   }

   "convert to an integer using correct bit shifting"  {
      RGBColor(239, 223, 207).toARGBInt() shouldBe -1056817
      RGBColor(239, 223, 207, 0).toARGBInt() shouldBe 0xEFDFCF
   }

   "convert to awt.color"  {
      Color(255, 0, 255) shouldBe RGBColor(255, 0, 255).toAWT()
      Color(255, 250, 255) shouldBe RGBColor(255, 250, 255).toAWT()
      Color(14, 250, 255) shouldBe RGBColor(14, 250, 255).toAWT()
      Color(255, 0, 0) shouldBe RGBColor(255, 0, 0).toAWT()
   }

   "convert from awt.color"  {
      RGBColor.fromAwt(Color(255, 0, 255)) shouldBe RGBColor(255, 0, 255)
      RGBColor.fromAwt(Color(255, 250, 255)) shouldBe RGBColor(255, 250, 255)
      RGBColor.fromAwt(Color(14, 250, 255)) shouldBe RGBColor(14, 250, 255)
      RGBColor.fromAwt(Color(255, 0, 0)) shouldBe RGBColor(255, 0, 0)
   }

   "convert from int to color using correct bit shifting"  {
      RGBColor.fromARGBInt(1088511) shouldBe RGBColor(16, 155, 255, 0)
   }

   "convert to HSV" {
      RGBColor(59, 68, 127).toHSV().apply {
         this.alpha shouldBe 1.0f
         this.hue.toInt() shouldBe 232
         this.saturation shouldBe (0.535F plusOrMinus 0.01F)
         this.value shouldBe (0.498F plusOrMinus 0.01F)
      }
      RGBColor(255, 255, 255).toHSV() shouldBe HSVColor(0F, 0F, 1F, 1F)
   }

   "toHSV preserves alpha from RGB" {
      RGBColor(59, 68, 127, 128).toHSV().alpha shouldBe (128 / 255f plusOrMinus 0.001f)
      RGBColor(59, 68, 127, 0).toHSV().alpha shouldBe 0f
      RGBColor(59, 68, 127, 255).toHSV().alpha shouldBe 1f
   }

   "toHSL preserves alpha from RGB" {
      RGBColor(59, 68, 127, 128).toHSL().alpha shouldBe (128 / 255f plusOrMinus 0.001f)
      RGBColor(59, 68, 127, 0).toHSL().alpha shouldBe 0f
      RGBColor(59, 68, 127, 255).toHSL().alpha shouldBe 1f
   }

   "convert to HSV and back to RGB" {
      val rgb = RGBColor(255, 255, 255)
      rgb.toHSV().toRGB() shouldBe rgb
   }

   // Regression: Color.paint() used new java.awt.Color(toARGBInt()) which calls the
   // single-int constructor that forces alpha=0xFF, silently dropping the color's alpha.
   // Fix: pass hasAlpha=true so the constructor reads alpha from bits 24-31.
   "paint() preserves alpha for semi-transparent color" {
      val halfTransparentRed = RGBColor(255, 0, 0, 128)
      val paint = halfTransparentRed.paint() as java.awt.Color
      paint.alpha shouldBe 128
      paint.red shouldBe 255
      paint.green shouldBe 0
      paint.blue shouldBe 0
   }

   "paint() preserves alpha for fully transparent color" {
      val transparent = RGBColor(0, 255, 0, 0)
      val paint = transparent.paint() as java.awt.Color
      paint.alpha shouldBe 0
   }

   "paint() preserves alpha for fully opaque color" {
      val opaque = RGBColor(0, 0, 255, 255)
      val paint = opaque.paint() as java.awt.Color
      paint.alpha shouldBe 255
   }

   // hashCode contract: equal objects must produce equal hash codes.
   // The optimised hand-coded hashCode replaces Objects.hash(varargs...)
   // to avoid per-call Integer[] allocation; the contract must still hold.
   "hashCode is consistent with equals" {
      val a = RGBColor(10, 20, 30, 40)
      val b = RGBColor(10, 20, 30, 40)
      a shouldBe b
      a.hashCode() shouldBe b.hashCode()
   }

   "hashCode distinguishes colors that differ in any channel" {
      val base = RGBColor(10, 20, 30, 40)
      // Not a strict requirement of the contract, but the 31*hash+field
      // pattern over four small ints does produce distinct values here.
      val differR = RGBColor(11, 20, 30, 40)
      val differG = RGBColor(10, 21, 30, 40)
      val differB = RGBColor(10, 20, 31, 40)
      val differA = RGBColor(10, 20, 30, 41)
      val all = setOf(base.hashCode(), differR.hashCode(), differG.hashCode(), differB.hashCode(), differA.hashCode())
      all.size shouldBe 5
   }

   "hashCode is stable across repeated calls" {
      val c = RGBColor(123, 45, 67, 89)
      val h = c.hashCode()
      c.hashCode() shouldBe h
      c.hashCode() shouldBe h
   }
})
