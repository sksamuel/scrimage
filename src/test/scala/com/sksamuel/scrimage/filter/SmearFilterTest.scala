package com.sksamuel.scrimage.filter

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.filter.SmearType.{Circles, Crosses, Squares}

/** @author Stephen Samuel */
class SmearFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    val original = Image(getClass.getResourceAsStream("/bird_small.png"))
    val expected1 = getClass.getResourceAsStream("/bird_small_smear_crosses.png")
    val expected2 = getClass.getResourceAsStream("/bird_small_smear_circles.png")
    val expected3 = getClass.getResourceAsStream("/bird_small_smear_squares.png")

    test("filter output matches expected") {
        assert(original.filter(SmearFilter(Crosses)) === Image(expected1))
        assert(original.filter(SmearFilter(Circles)) === Image(expected2))
        assert(original.filter(SmearFilter(Squares)) === Image(expected3))
    }
}
