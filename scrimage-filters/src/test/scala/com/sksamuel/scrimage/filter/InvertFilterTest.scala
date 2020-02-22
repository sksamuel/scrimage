package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class InvertFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = getClass.getResourceAsStream("/bird_small.png")
  val expected = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_invert.png")

  test("filter output matches expected") {
    assert(ImmutableImage.fromStream(original).filter(new InvertFilter) === ImmutableImage.fromStream(expected))
  }
}
