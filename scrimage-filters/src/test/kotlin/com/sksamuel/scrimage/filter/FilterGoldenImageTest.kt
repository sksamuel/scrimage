package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Golden image regression tests for the filters whose hot loops were optimised
 * (loop-invariant hoisting in the jhlabs EdgeFilter, SparkleFilter and
 * ColorHalftoneFilter). The optimisation must be bit-identical, so each filter
 * output is compared pixel-for-pixel against a golden PNG generated before the
 * change ([ImmutableImage.equals] compares dimensions and every ARGB value).
 *
 * The golden images and default-constructor parameters mirror the Scala specs
 * (EdgeFilterTest.scala, SparkleFilterTest.scala, ColorHalftoneFilterTest.scala),
 * which are not wired into the Gradle build. SparkleFilter is deterministic
 * despite using java.util.Random: the jhlabs implementation seeds it with a
 * fixed seed (371) in setDimensions before every run.
 */
class FilterGoldenImageTest : FunSpec({

   val original = ImmutableImage.loader().fromResource("/bird_small.png")

   test("EdgeFilter output matches golden image pixel-for-pixel") {
      val expected = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/filters/bird_small_edge.png")
      original.filter(EdgeFilter()) shouldBe expected
   }

   test("SparkleFilter output matches golden image pixel-for-pixel") {
      val expected = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/filters/bird_small_sparkle.png")
      original.filter(SparkleFilter()) shouldBe expected
   }

   test("ColorHalftoneFilter output matches golden image pixel-for-pixel") {
      val expected = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/filters/bird_small_color_halftone.png")
      original.filter(ColorHalftoneFilter()) shouldBe expected
   }
})
