package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

/**
 * Pins down the hashCode contract for Pixel after the hand-coded
 * hashCode replaced Objects.hash(varargs...) to avoid per-call
 * Integer[] allocation.
 */
class PixelHashCodeTest : StringSpec({

   "hashCode is consistent with equals" {
      val a = Pixel(3, 4, 0xFF112233.toInt())
      val b = Pixel(3, 4, 0xFF112233.toInt())
      a shouldBe b
      a.hashCode() shouldBe b.hashCode()
   }

   "hashCode distinguishes pixels that differ in any field" {
      val base = Pixel(3, 4, 0xFF112233.toInt())
      val differArgb = Pixel(3, 4, 0xFF112234.toInt())
      val differX = Pixel(2, 4, 0xFF112233.toInt())
      val differY = Pixel(3, 5, 0xFF112233.toInt())
      val all = setOf(base.hashCode(), differArgb.hashCode(), differX.hashCode(), differY.hashCode())
      all.size shouldBe 4
   }

   "hashCode is stable across repeated calls" {
      val p = Pixel(7, 8, 0xCAFEBABE.toInt())
      val h = p.hashCode()
      p.hashCode() shouldBe h
      p.hashCode() shouldBe h
   }
})
