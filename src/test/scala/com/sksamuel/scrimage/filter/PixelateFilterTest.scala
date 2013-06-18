package com.sksamuel.scrimage.filter

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import com.sksamuel.scrimage.Image

/** @author Stephen Samuel */
class PixelateFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    val original = Image(getClass.getResourceAsStream("/bird_small.png"))
    val expected2 = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_block_2.png")
    val expected4 = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_block_4.png")

    test("filter output matches expected") {
        assert(original.filter(PixelateFilter(2)) === Image(expected2))
        assert(original.filter(PixelateFilter(4)) === Image(expected4))
    }
}
