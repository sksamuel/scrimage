package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class MaximumFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = getClass.getResourceAsStream("/bird_small.png")
  val expected = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_maximum.png")

  test("max filter output matches expected") {
    assert(ImmutableImage.fromStream(original).filter(new MaximumFilter) === ImmutableImage.fromStream(expected))
  }
}
