package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import org.scalatest.{ BeforeAndAfter, FunSuite, OneInstancePerTest }

/** @author Stephen Samuel */
class SnowFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = Image(getClass.getResourceAsStream("/bird_small.png"))

  test("snow filter output matches expected") {
    val expected = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_snow.png"))
    val actual = original.filter(SnowFilter)
    assert(actual === expected)
  }
}
