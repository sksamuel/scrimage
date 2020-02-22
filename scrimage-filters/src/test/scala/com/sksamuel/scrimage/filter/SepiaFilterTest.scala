package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class SepiaFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  private val original = ImmutableImage.fromResource("/bird_small.png")

  test("filter output matches expected") {
    val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_sepia.png")
    assert(original.filter(new SepiaFilter) === expected)
  }
}
