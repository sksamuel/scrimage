package com.sksamuel.scrimage.filter

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import com.sksamuel.scrimage.Image

/** @author Stephen Samuel */
class GammaFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    val original = Image(getClass.getResourceAsStream("/bird_small.png"))

    test("filter output matches expected") {
        //   original.filter(GammaFilter(2)).write(new File("src/test/resources/bird_small_gamma.png"))
        val expected = getClass.getResourceAsStream("/bird_small_gamma.png")
        assert(original.filter(GammaFilter(2)) === Image(expected))
    }
}
