package com.sksamuel.scrimage.core.color

import com.sksamuel.scrimage.color.CMYKColor
import com.sksamuel.scrimage.color.Grayscale
import com.sksamuel.scrimage.color.HSLColor
import com.sksamuel.scrimage.color.HSVColor
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Pins down the hashCode contract for Grayscale/CMYK/HSL/HSV after the
 * hand-coded hashCode replaced Objects.hash(varargs...) to avoid
 * per-call Object[] allocation (with autoboxing of int and float fields).
 */
class ColorHashCodeTest : StringSpec({

   "Grayscale hashCode is consistent with equals" {
      val a = Grayscale(128, 200)
      val b = Grayscale(128, 200)
      a shouldBe b
      a.hashCode() shouldBe b.hashCode()
   }

   "Grayscale hashCode distinguishes by gray and alpha" {
      Grayscale(128, 200).hashCode() shouldNotBe Grayscale(129, 200).hashCode()
      Grayscale(128, 200).hashCode() shouldNotBe Grayscale(128, 201).hashCode()
   }

   "CMYKColor hashCode is consistent with equals" {
      val a = CMYKColor(0.1f, 0.2f, 0.3f, 0.4f, 0.5f)
      val b = CMYKColor(0.1f, 0.2f, 0.3f, 0.4f, 0.5f)
      a shouldBe b
      a.hashCode() shouldBe b.hashCode()
   }

   "CMYKColor hashCode distinguishes by every channel" {
      val base = CMYKColor(0.1f, 0.2f, 0.3f, 0.4f, 0.5f)
      val all = setOf(
         base.hashCode(),
         CMYKColor(0.11f, 0.2f, 0.3f, 0.4f, 0.5f).hashCode(),
         CMYKColor(0.1f, 0.21f, 0.3f, 0.4f, 0.5f).hashCode(),
         CMYKColor(0.1f, 0.2f, 0.31f, 0.4f, 0.5f).hashCode(),
         CMYKColor(0.1f, 0.2f, 0.3f, 0.41f, 0.5f).hashCode(),
         CMYKColor(0.1f, 0.2f, 0.3f, 0.4f, 0.51f).hashCode()
      )
      all.size shouldBe 6
   }

   "HSLColor hashCode is consistent with equals" {
      val a = HSLColor(180f, 0.5f, 0.5f, 1f)
      val b = HSLColor(180f, 0.5f, 0.5f, 1f)
      a shouldBe b
      a.hashCode() shouldBe b.hashCode()
   }

   "HSLColor hashCode distinguishes by every channel" {
      val base = HSLColor(180f, 0.5f, 0.5f, 1f)
      val all = setOf(
         base.hashCode(),
         HSLColor(181f, 0.5f, 0.5f, 1f).hashCode(),
         HSLColor(180f, 0.6f, 0.5f, 1f).hashCode(),
         HSLColor(180f, 0.5f, 0.6f, 1f).hashCode(),
         HSLColor(180f, 0.5f, 0.5f, 0.9f).hashCode()
      )
      all.size shouldBe 5
   }

   "HSVColor hashCode is consistent with equals" {
      val a = HSVColor(180f, 0.5f, 0.5f, 1f)
      val b = HSVColor(180f, 0.5f, 0.5f, 1f)
      a shouldBe b
      a.hashCode() shouldBe b.hashCode()
   }

   "HSVColor hashCode distinguishes by every channel" {
      val base = HSVColor(180f, 0.5f, 0.5f, 1f)
      val all = setOf(
         base.hashCode(),
         HSVColor(181f, 0.5f, 0.5f, 1f).hashCode(),
         HSVColor(180f, 0.6f, 0.5f, 1f).hashCode(),
         HSVColor(180f, 0.5f, 0.6f, 1f).hashCode(),
         HSVColor(180f, 0.5f, 0.5f, 0.9f).hashCode()
      )
      all.size shouldBe 5
   }
})
