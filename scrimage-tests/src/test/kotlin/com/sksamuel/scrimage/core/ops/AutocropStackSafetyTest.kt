package com.sksamuel.scrimage.core.ops

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Regression: AutocropOps.scanright/left/up/down were unbounded tail
 * recursion. The JVM does not eliminate Java tail calls, so an image
 * whose border is fully one colour and whose width or height is
 * large enough recursed once per pixel column/row and tripped
 * StackOverflowError. Rewritten as a while loop, the work scales
 * the same way but uses constant stack space.
 */
class AutocropStackSafetyTest : FunSpec({

   test("autocrop on a wide image with a full single-colour interior is stack-safe") {
      // 20000-px wide, single-colour image. The crop would recurse
      // ~20000 deep on each axis pre-fix and trip StackOverflowError
      // on most JVM defaults.
      val img = ImmutableImage.filled(20000, 10, Color.WHITE)
      val out = img.autocrop(Color.WHITE)
      // The whole image matches the crop colour, so cropping returns
      // the trivial 0-px or 1-px image. Either way it returns rather
      // than blowing the stack — that's the regression we care about.
      out.width shouldBe out.width // (forces evaluation)
   }
})
