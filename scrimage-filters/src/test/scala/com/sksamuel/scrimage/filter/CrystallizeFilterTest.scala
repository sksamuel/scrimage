package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PngWriter
import org.scalatest.{FunSuite, Matchers}

class CrystallizeFilterTest extends FunSuite with Matchers {

  implicit private val writer: PngWriter = PngWriter.MaxCompression
  private val original = ImmutableImage.fromResource("/bird_small.png")

  test("filter output matches expected") {
    val actual = original.filter(new CrystallizeFilter(16, 0.4, 0xff000000, 0))
    val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_crystallize.png")
    actual shouldBe expected
  }

  test("support edge thickness") {
    val actual = original.filter(new CrystallizeFilter(16, 0.6, 0xff000000, 0))
    val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_crystallize_edge_thickeness.png")
    actual shouldBe expected
  }

  test("support edge colour") {
    val actual = original.filter(new CrystallizeFilter(16, 0.6, 0xff00ff00, 0))
    val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_crystallize_edge_colour.png")
    actual shouldBe expected
  }

  test("support scale") {
    val actual = original.filter(new CrystallizeFilter(12, 0.4, 0xff000000, 0))
    val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_crystallize_scale.png")
    actual shouldBe expected
  }

  test("support randomness") {
    original.filter(new CrystallizeFilter(16, 0.8, 0xff000000, 0.2)) should not be
      original.filter(new CrystallizeFilter(16, 0.8, 0xff000000, 0.4))
  }
}
