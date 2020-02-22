package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class SharpenFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  private val original = ImmutableImage.fromResource("/bird_small.png")
  private val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_sharpen.png")

  test("filter output matches expected") {
    assert(original.copy.filter(new SharpenFilter) === expected)
  }
}
