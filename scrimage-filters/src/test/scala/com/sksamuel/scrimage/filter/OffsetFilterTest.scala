package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class OffsetFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = ImmutableImage.fromStream(getClass.getResourceAsStream("/bird_small.png"))

  test("filter output matches expected") {
    val expected = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_offset.png")
    assert(original.filter(new OffsetFilter(40, 60)) === ImmutableImage.fromStream(expected))
  }
}
