package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import org.scalatest.FunSuite

class VignetteFilterTest extends FunSuite {

  private val original = Image.fromResource("/bird_small.png")

  test("filter output matches expected") {
    val expected = Image.fromResource("/com/sksamuel/scrimage/filters/bird_small_vignette.png")
    val actual = original.filter(new VignetteFilter())
    assert(actual === expected)
  }
}
