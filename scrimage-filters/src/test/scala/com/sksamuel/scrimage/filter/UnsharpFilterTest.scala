package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import org.scalatest.FunSuite

class UnsharpFilterTest extends FunSuite {

  private val original = getClass.getResourceAsStream("/bird_small.png")
  private val expected = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_unsharp.png")

  test("filter output matches expected") {
    assert(ImmutableImage.fromStream(original).filter(new UnsharpFilter()) === ImmutableImage.fromStream(expected))
  }
}
