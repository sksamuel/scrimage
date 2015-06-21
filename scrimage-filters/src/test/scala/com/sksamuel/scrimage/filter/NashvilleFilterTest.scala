package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import org.scalatest.FunSuite

/** @author Stephen Samuel */
class NashvilleFilterTest extends FunSuite {

  val original = Image.fromResource("/bird_small.png")

  // todo this filter is crap, should re-work it
  test("filter output matches expected") {
    val actual = original.filter(NashvilleFilter)
    assert(actual === Image.fromResource("/com/sksamuel/scrimage/filters/bird_small_nashville.png"))
  }
}
