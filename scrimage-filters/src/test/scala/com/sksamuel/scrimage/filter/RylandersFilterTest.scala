package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class RylandersFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = ImmutableImage.fromStream(getClass.getResourceAsStream("/bird_small.png"))

  test("filter output matches expected") {
    val expected = ImmutableImage.fromStream(getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_rylanders.png"))
    assert(original.filter(new RylandersFilter) === expected)
  }
}
