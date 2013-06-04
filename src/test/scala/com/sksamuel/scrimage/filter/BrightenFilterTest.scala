package com.sksamuel.scrimage.filter

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import com.sksamuel.scrimage.Image

/** @author Stephen Samuel */
class BrightenFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    val original = getClass.getResourceAsStream("/bird_small.png")
    val expected = getClass.getResourceAsStream("/bird_small_brighten.png")

    test("filter output matches expected") {
        //Image(original).filter(BrightenFilter(1.3)).write(new File("src/test/resources/bird_small_brighten.png"))
        assert(Image(original).filter(BrightenFilter(1.3)) === Image(expected))
    }
}
