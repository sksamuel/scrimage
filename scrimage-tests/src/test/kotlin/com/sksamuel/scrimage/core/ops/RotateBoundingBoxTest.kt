package com.sksamuel.scrimage.core.ops

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.angles.Degrees
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Regression: `AwtImage.rotateByRadians` used `Math.floor` for the
 * axis-aligned bounding box of the rotated image. The bbox of a
 * w x h rectangle rotated by θ is `w*|cos θ| + h*|sin θ|`; flooring
 * that and then placing the rotated image at the centre clipped the
 * corner pixels by a sub-pixel that compounded into a missing pixel
 * along the edge.
 *
 * Example: a 10x10 image at 45° has bbox 10√2 ≈ 14.142. Floor gives
 * a 14x14 canvas — the rotated corners stick out by ~0.07 px on each
 * side and are clipped. Ceil gives 15x15 and preserves them.
 */
class RotateBoundingBoxTest : FunSpec({

   test("rotating a 10x10 image by 45° produces a 15x15 canvas, not 14x14") {
      val src = ImmutableImage.filled(10, 10, Color.RED)
      val rotated = src.rotate(Degrees(45))
      // bbox = 10 * √2 ≈ 14.142 → ceil = 15. Pre-fix this returned 14.
      rotated.width shouldBe 15
      rotated.height shouldBe 15
   }

   test("rotating a 100x50 image by 30° rounds dimensions up") {
      val src = ImmutableImage.filled(100, 50, Color.RED)
      val rotated = src.rotate(Degrees(30))
      // bbox_w = 100*cos30 + 50*sin30 = 86.60 + 25 = 111.60 → 112
      // bbox_h = 50*cos30 + 100*sin30 = 43.30 + 50 = 93.30 → 94
      rotated.width shouldBe 112
      rotated.height shouldBe 94
   }

   test("rotating a square by 90° still produces an exact-dimension square") {
      // sin & cos are exact (0, 1, 0, -1) at the cardinal angles, so
      // ceil and floor agree there — confirm we didn't regress that.
      val src = ImmutableImage.filled(10, 10, Color.RED)
      val rotated = src.rotate(Degrees(90))
      rotated.width shouldBe 10
      rotated.height shouldBe 10
   }
})
