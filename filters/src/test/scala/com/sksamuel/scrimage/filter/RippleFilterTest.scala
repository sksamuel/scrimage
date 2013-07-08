package com.sksamuel.scrimage.filter

import org.scalatest.{ OneInstancePerTest, BeforeAndAfter, FunSuite }
import com.sksamuel.scrimage.Image

/** @author Stephen Samuel */
class RippleFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = Image(getClass.getResourceAsStream("/bird_small.png"))
  val expected1 = getClass.getResourceAsStream("/bird_ripple_triangle.png")
  val expected2 = getClass.getResourceAsStream("/bird_ripple_sawtooth.png")

  test("filter output matches expected") {
    assert(original.filter(RippleFilter(RippleType.Triangle)) === Image(expected1))
    assert(original.filter(RippleFilter(RippleType.Sawtooth)) === Image(expected2))
  }
}
