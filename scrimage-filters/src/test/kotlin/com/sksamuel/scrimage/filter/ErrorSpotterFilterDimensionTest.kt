package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import java.awt.Color

class ErrorSpotterFilterDimensionTest : FunSpec({

   // Regression: ErrorSpotterFilter.apply previously validated dimension
   // matches via `assert`, which is disabled by default in production JVMs.
   // A dimension mismatch then surfaced as a bare ArrayIndexOutOfBoundsException
   // from base.awt().getRGB(x, y) deep inside the apply loop, with no hint
   // about which inputs caused it.

   test("apply throws IllegalArgumentException with both dimensions when widths differ") {
      val base = ImmutableImage.filled(50, 30, Color.RED)
      val src = ImmutableImage.filled(40, 30, Color.BLUE)
      val filter = ErrorSpotterFilter(base)
      val ex = shouldThrow<IllegalArgumentException> { src.filter(filter) }
      ex.message!!.shouldContain("src=40x30")
      ex.message!!.shouldContain("base=50x30")
   }

   test("apply throws IllegalArgumentException with both dimensions when heights differ") {
      val base = ImmutableImage.filled(40, 50, Color.RED)
      val src = ImmutableImage.filled(40, 30, Color.BLUE)
      val filter = ErrorSpotterFilter(base)
      val ex = shouldThrow<IllegalArgumentException> { src.filter(filter) }
      ex.message!!.shouldContain("src=40x30")
      ex.message!!.shouldContain("base=40x50")
   }

   test("apply succeeds when dimensions match") {
      val base = ImmutableImage.filled(40, 30, Color.RED)
      val src = ImmutableImage.filled(40, 30, Color.BLUE)
      val filter = ErrorSpotterFilter(base)
      // Just verify it runs without throwing.
      src.filter(filter)
   }
})
