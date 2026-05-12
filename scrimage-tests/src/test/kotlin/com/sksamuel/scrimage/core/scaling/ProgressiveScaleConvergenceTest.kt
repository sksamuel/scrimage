package com.sksamuel.scrimage.core.scaling

import com.sksamuel.scrimage.ProgressiveScale
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.RenderingHints
import java.awt.image.BufferedImage

/**
 * Regression: ProgressiveScale's loop only halved an axis when it was
 * greater than the target. If a caller invoked it with any target
 * dimension >= the source dimension on that axis (e.g. target wider
 * than source but shorter than source), that axis never converged
 * and the do/while spun forever, each iteration allocating a new
 * BufferedImage.
 *
 * The fix snaps each axis up to the target if it's already at-or-below,
 * short-circuits when both axes match the target, and validates targets
 * are positive.
 */
class ProgressiveScaleConvergenceTest : FunSpec({

   fun source(): BufferedImage = BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB)

   test("target equal to source returns the source without allocating") {
      val src = source()
      val out = ProgressiveScale.scale(src, 100, 100, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
      // Identity case: no progressive step needed.
      out shouldBe src
   }

   test("target taller than source on one axis converges (no infinite loop)") {
      // Pre-fix this hung forever because h (100) >= targetHeight (200)
      // meant the h-halving branch never fired and `w != targetWidth ||
      // h != targetHeight` was always true after w reached targetWidth.
      val src = source()
      val out = ProgressiveScale.scale(src, 50, 200, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
      out.width shouldBe 50
      out.height shouldBe 200
   }

   test("target >= source on both axes returns source (downscale-only, no infinite loop)") {
      // ProgressiveScale is a downscale algorithm — halve until target.
      // For a target larger than source on both axes there is no
      // progressive step to run, so the source is returned. Pre-fix
      // this spun forever, each iteration reallocating BufferedImages.
      val src = source()
      val out = ProgressiveScale.scale(src, 200, 200, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
      out shouldBe src
   }

   test("target dimensions must be positive") {
      shouldThrow<IllegalArgumentException> {
         ProgressiveScale.scale(source(), 0, 50, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
      }
      shouldThrow<IllegalArgumentException> {
         ProgressiveScale.scale(source(), 50, -1, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
      }
   }
})
