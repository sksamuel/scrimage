package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class MotionBlurFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  private val original = ImmutableImage.fromResource("/bird_small.png")

  test("motion blur filter output matches expected") {
    val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_motionblur.png")
    assert(original.filter(new MotionBlurFilter(Math.PI / 3.0, 25)) === expected)
  }
}
