package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class RaysFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  private val original = getClass.getResourceAsStream("/bird_small.png")
  private val expected = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_rays.png")

  test("filter output matches expected") {
    assert(ImmutableImage.fromStream(original).filter(new RaysFilter(1.0f, 0.1f, 0.6f)) === ImmutableImage.fromStream(expected))
  }
}
