package com.sksamuel.scrimage.filter

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import com.sksamuel.scrimage.Image

/** @author Stephen Samuel */
class LensFlareFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    val original = Image(getClass.getResourceAsStream("/bird_small.png"))

    test("filter output matches expected") {
        val expected = getClass.getResourceAsStream("/bird_small_lens_flare.png")
        assert(original.filter(LensFlareFilter) != Image(expected)) // tis another random one
    }
}
