package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.filter.SmearType.{Circles, Crosses, Squares}
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class SmearFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = Image.fromStream(getClass.getResourceAsStream("/bird_small.png"))
  val expected1 = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_smear_crosses.png")
  val expected2 = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_smear_circles.png")
  val expected3 = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_smear_squares.png")

  test("filter output matches expected") {
    assert(original.filter(new SmearFilter(Crosses)) === Image.fromStream(expected1))
    assert(original.filter(new SmearFilter(Circles)) === Image.fromStream(expected2))
    assert(original.filter(new SmearFilter(Squares)) === Image.fromStream(expected3))
  }
}
