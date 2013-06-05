package com.sksamuel.scrimage.filter

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import com.sksamuel.scrimage.Image

/** @author Stephen Samuel */
class RaysFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    val original = getClass.getResourceAsStream("/bird_small.png")
    val expected = getClass.getResourceAsStream("/bird_small_rays.png")

    test("filter output matches expected") {
        assert(Image(original).filter(RaysFilter(threshold = 0.1f, strength = 0.6f)) === Image(expected))
    }
}
