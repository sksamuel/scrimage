package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class VintageFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  private val original = Image(getClass.getResourceAsStream("/bird_small.png"))
  private val expected = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_vintage.png"))

  test("filter output matches expected") {
    val actual = original.filter(new VintageFilter)
    assert(actual === expected)
  }
}
