package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import org.scalatest.FunSuite

class HsbFilterTest extends FunSuite {

  private val original = Image.fromStream(getClass.getResourceAsStream("/bird_small.png"))
  private val expected = Image.fromStream(getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_hue.png"))

  test("filter output matches expected") {
    assert(original.filter(new HSBFilter(0.5f, 0, 0)) === expected)
  }
}
