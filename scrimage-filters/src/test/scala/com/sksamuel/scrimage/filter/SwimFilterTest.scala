package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class SwimFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  private val original = Image.fromResource("/bird_small.png")

  test("swim filter output matches expected") {
    val expected = Image.fromResource("/com/sksamuel/scrimage/filters/bird_small_swim.png")
    assert(original.copy.filter(new SwimFilter()) != expected)
  }
}
