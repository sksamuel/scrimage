package com.sksamuel.scrimage.core.invariants

import com.sksamuel.scrimage.color.RGBColor
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.math.abs

/**
 * Property-style invariants for the colour conversion graph:
 *
 *   ARGB int  ⇄  RGBColor          (lossless)
 *   awt.Color ⇄  RGBColor          (lossless)
 *   RGBColor → HSL → RGBColor      (≈ within ±2 per channel due to rounding)
 *   RGBColor → HSV → RGBColor      (≈ within ±2 per channel)
 *   RGBColor → CMYK → RGBColor     (≈ within ±2 per channel)
 *
 * The lossless ARGB and AWT round-trips must be bit-exact. The HSL / HSV /
 * CMYK round-trips lose a tiny amount of precision because each channel
 * is quantised to 8 bits at both ends of the conversion; we allow ±2 to
 * absorb the typical Math.round error around grayscale/saturation cusps.
 *
 * Sampled across a deterministic grid of colours that includes the
 * extremes (black, white, primaries) and a uniform interior sample.
 */
class ColorRoundTripInvariantTest : FunSpec({

   // Construct a representative grid of opaque RGBColors. Sweeps every
   // channel through {0, 51, 102, 153, 204, 255} = 6 levels per channel
   // = 216 colours, plus the four channel extremes.
   fun grid(): List<RGBColor> {
      val levels = listOf(0, 51, 102, 153, 204, 255)
      val out = mutableListOf<RGBColor>()
      for (r in levels) for (g in levels) for (b in levels) {
         out.add(RGBColor(r, g, b, 255))
      }
      return out
   }

   // Same grid but with varying alpha.
   fun gridWithAlpha(): List<RGBColor> {
      val out = mutableListOf<RGBColor>()
      val levels = listOf(0, 64, 128, 192, 255)
      for (r in levels) for (g in levels) for (b in levels) for (a in levels) {
         out.add(RGBColor(r, g, b, a))
      }
      return out
   }

   fun within(actual: Int, expected: Int, tolerance: Int): Boolean {
      return abs(actual - expected) <= tolerance
   }

   test("RGBColor → ARGB int → RGBColor is bit-exact") {
      for (c in gridWithAlpha()) {
         val packed = c.toARGBInt()
         val back = RGBColor.fromARGBInt(packed)
         back.red shouldBe c.red
         back.green shouldBe c.green
         back.blue shouldBe c.blue
         back.alpha shouldBe c.alpha
      }
   }

   test("RGBColor → java.awt.Color → RGBColor is bit-exact") {
      for (c in gridWithAlpha()) {
         val awt = c.toAWT()
         val back = RGBColor.fromAwt(awt)
         back.red shouldBe c.red
         back.green shouldBe c.green
         back.blue shouldBe c.blue
         back.alpha shouldBe c.alpha
      }
   }

   test("RGBColor → HSL → RGBColor preserves each channel within ±2") {
      for (c in grid()) {
         val back = c.toHSL().toRGB()
         within(back.red, c.red, 2) shouldBe true
         within(back.green, c.green, 2) shouldBe true
         within(back.blue, c.blue, 2) shouldBe true
         back.alpha shouldBe c.alpha
      }
   }

   test("RGBColor → HSV → RGBColor preserves each channel within ±2") {
      for (c in grid()) {
         val back = c.toHSV().toRGB()
         within(back.red, c.red, 2) shouldBe true
         within(back.green, c.green, 2) shouldBe true
         within(back.blue, c.blue, 2) shouldBe true
         back.alpha shouldBe c.alpha
      }
   }

   test("RGBColor → CMYK → RGBColor preserves each channel within ±2") {
      for (c in grid()) {
         val back = c.toCMYK().toRGB()
         within(back.red, c.red, 2) shouldBe true
         within(back.green, c.green, 2) shouldBe true
         within(back.blue, c.blue, 2) shouldBe true
         back.alpha shouldBe c.alpha
      }
   }
})
