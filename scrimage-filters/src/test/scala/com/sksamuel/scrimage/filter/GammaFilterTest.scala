package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class GammaFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = ImmutableImage.fromStream(getClass.getResourceAsStream("/bird_small.png"))

  test("gamma filter output matches expected") {
    val expected = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_gamma.png")
    assert(original.filter(new GammaFilter(2)) === ImmutableImage.fromStream(expected))
  }
}
