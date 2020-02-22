package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import org.scalatest.FunSuite

class NashvilleFilterTest extends FunSuite {

  private val original = ImmutableImage.fromResource("/bird_small.png")

  // todo this filter is crap, should re-work it
  test("filter output matches expected") {
    val actual = original.filter(new NashvilleFilter)
    assert(actual === ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_nashville.png"))
  }
}
