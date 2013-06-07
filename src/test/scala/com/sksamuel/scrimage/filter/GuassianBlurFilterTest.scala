package com.sksamuel.scrimage.filter

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import com.sksamuel.scrimage.Image

/** @author Stephen Samuel */
class GuassianBlurFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    val original = Image(getClass.getResourceAsStream("/bird_small.png"))

    test("filter output matches expected") {
        //Image(original).filter(GaussianBlurFilter(6)).write(new File("src/test/resources/bird_small_guassian.png"))
        val expected = Image(getClass.getResourceAsStream("/bird_small_guassian.png"))
        assert(original.filter(GaussianBlurFilter(6)) === expected)
    }
}
