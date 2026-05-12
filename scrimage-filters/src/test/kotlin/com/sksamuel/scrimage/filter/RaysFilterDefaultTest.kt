package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import java.awt.Color

/**
 * Regression: RaysFilter exposed only (opacity, threshold, strength).
 * jhlabs RaysFilter extends MotionBlurOp; the rays are produced by the
 * parent's motion-blur step, which depends on (angle, distance,
 * zoom, rotation). Those all defaulted to 0, so MotionBlurOp short-
 * circuited with maxDistance == 0 and the filter returned a near-copy
 * of the thresholded luminance image with no radial component.
 *
 * The wrapper now seeds a non-zero default zoom so the filter
 * produces visible rays out of the box.
 */
class RaysFilterDefaultTest : FunSpec({

   test("default RaysFilter produces output that differs from the source") {
      // Source: a small bright spot on a dark background — exactly the
      // input the rays filter is designed for.
      val src = ImmutableImage.create(100, 100).map { p ->
         val cx = 50; val cy = 50
         val dx = p.x() - cx; val dy = p.y() - cy
         if (dx*dx + dy*dy < 10*10) Color(255, 255, 255) else Color.BLACK
      }
      val rays = src.filter(RaysFilter())
      // Pre-fix the rays filter produced a near-copy of the source
      // (the only step was the threshold pass through MotionBlurOp
      // with maxDistance == 0).
      rays shouldNotBe src
   }
})
