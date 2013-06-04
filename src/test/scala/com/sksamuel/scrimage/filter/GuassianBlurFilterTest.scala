package com.sksamuel.scrimage.filter

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import com.sksamuel.scrimage.Image
import java.io.File

/** @author Stephen Samuel */
class GuassianBlurFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    val original = getClass.getResourceAsStream("/bird_small.png")
    val expected = getClass.getResourceAsStream("/bird_small_guassian.png")

    test("filter output matches expected") {
        //Image(original).filter(GaussianBlurFilter(6)).write(new File("src/test/resources/bird_small_guassian.png"))
        assert(Image(original).filter(GaussianBlurFilter(6)) === Image(expected))
    }
}
