package com.sksamuel.scrimage.filter

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.filter.SmearType.Circles

/** @author Stephen Samuel */
class SmearFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    val original = getClass.getResourceAsStream("/bird_small.png")
    val expected = getClass.getResourceAsStream("/bird_small_smear_squares.png")

    test("filter output matches expected") {
        assert(Image(original).filter(SmearFilter(Circles)) === Image(expected))
    }
}
