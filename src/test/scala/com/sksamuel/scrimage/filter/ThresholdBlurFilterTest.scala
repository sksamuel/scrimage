package com.sksamuel.scrimage.filter

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import com.sksamuel.scrimage.Image

/** @author Stephen Samuel */
class ThresholdBlurFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    val original = getClass.getResourceAsStream("/bird_small.png")
    val expected = getClass.getResourceAsStream("/bird_small_threshold.png")

    test("filter output matches expected") {
        assert(Image(original).filter(ThresholdFilter()) === Image(expected))
    }
}
