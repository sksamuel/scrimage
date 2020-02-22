package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class ColorizeFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  private val original = ImmutableImage.fromResource("/bird_small.png")

  test("filter output matches expected") {
    val expected = ImmutableImage.fromStream(getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_colorize.png"))
    assert(original.filter(new ColorizeFilter(255, 0, 0, 50)) === expected)
  }
}
