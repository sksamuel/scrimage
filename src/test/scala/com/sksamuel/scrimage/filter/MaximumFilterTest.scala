package com.sksamuel.scrimage.filter

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import com.sksamuel.scrimage.Image

/** @author Stephen Samuel */
class MaximumFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    val original = getClass.getResourceAsStream("/bird_small.png")
    val expected = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_maximum.png")

    test("filter output matches expected") {
        assert(Image(original).filter(MaximumFilter) === Image(expected))
    }
}
