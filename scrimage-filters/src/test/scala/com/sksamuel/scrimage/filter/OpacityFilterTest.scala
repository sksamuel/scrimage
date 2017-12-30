package com.sksamuel.scrimage.filter

import java.io.File

import com.sksamuel.scrimage.Image
import org.scalatest.{ BeforeAndAfter, FunSuite, OneInstancePerTest }

class OpacityFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = Image(getClass.getResourceAsStream("/bird_small.png"))

  test("opacity filter output matches expected") {
    val expected = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_opacity.png"))
    val actual = original.filter(new OpacityFilter(0.5f))
    assert(actual === expected)
  }
}
