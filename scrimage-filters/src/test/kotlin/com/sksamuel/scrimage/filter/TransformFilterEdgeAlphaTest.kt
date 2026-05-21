package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Regression: TransformFilter defaulted to RGB_CLAMP for off-edge
 * pixels, which preserves RGB but zeros alpha. Every subclass
 * (TwirlFilter, SwimFilter, RippleFilter, KaleidoscopeFilter, …) on
 * an opaque input produced transparent corners — visible holes when
 * the result was composited against any non-default background.
 *
 * The fix changes the default to CLAMP (preserve both RGB and
 * alpha).
 */
class TransformFilterEdgeAlphaTest : FunSpec({

   test("TwirlFilter on an opaque image preserves alpha at the corners") {
      val opaqueRed = ImmutableImage.filled(100, 100, Color.RED)
      val twirled = opaqueRed.filter(TwirlFilter(1.5f))

      // Corner pixels must still be opaque — pre-fix, alpha at (0, 0)
      // was 0 (RGB_CLAMP zero-alpha), so the corner had no colour
      // contribution when composited.
      val c00 = twirled.pixel(0, 0)
      val c99 = twirled.pixel(99, 99)
      c00.alpha() shouldBe 255
      c99.alpha() shouldBe 255
   }

   test("RippleFilter on an opaque image preserves alpha at the corners") {
      val opaqueRed = ImmutableImage.filled(100, 100, Color.RED)
      val rippled = opaqueRed.filter(RippleFilter(RippleType.Sine))

      rippled.pixel(0, 0).alpha() shouldBe 255
      rippled.pixel(99, 99).alpha() shouldBe 255
   }
})
