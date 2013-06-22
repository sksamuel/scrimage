package com.sksamuel.scrimage.filter

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import com.sksamuel.scrimage.Image

/** @author Stephen Samuel */
class SnowFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    val original = Image(getClass.getResourceAsStream("/bird_small.png"))

    test("filter output matches expected") {
        val expected = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_snow.png"))
        assert(original.filter(SnowFilter) === expected)
    }
}
