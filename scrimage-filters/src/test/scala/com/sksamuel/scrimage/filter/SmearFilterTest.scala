package com.sksamuel.scrimage.filter

import org.scalatest.{ OneInstancePerTest, BeforeAndAfter, FunSuite }
import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.filter.SmearType.{ Circles, Crosses, Squares }

class SmearFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = Image(getClass.getResourceAsStream("/bird_small.png"))
  val expected1 = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_smear_crosses.png")
  val expected2 = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_smear_circles.png")
  val expected3 = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_smear_squares.png")

  test("filter output matches expected") {
    assert(original.filter(new SmearFilter(Crosses)) === Image(expected1))
    assert(original.filter(new SmearFilter(Circles)) === Image(expected2))
    assert(original.filter(new SmearFilter(Squares)) === Image(expected3))
  }
}
