package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class ThresholdBlurFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  private val original = getClass.getResourceAsStream("/bird_small.png")
  private val expected = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_threshold.png")

  test("filter output matches expected") {
    assert(ImmutableImage.fromStream(original).filter(new ThresholdFilter()) === ImmutableImage.fromStream(expected))
  }
}
