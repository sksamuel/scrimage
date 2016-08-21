package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import org.scalatest.FunSuite

/** @author Stephen Samuel */
class VignetteFilterTest extends FunSuite {

  val original = Image.fromStream(getClass.getResourceAsStream("/bird_small.png"))

  test("filter output matches expected") {
    val expected = Image.fromStream(getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_vignette.png"))
    val actual = original.filter(VignetteFilter())
    assert(actual === expected)
  }
}
