package com.sksamuel.scrimage.filter

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import com.sksamuel.scrimage.Image

/** @author Stephen Samuel */
class GlowFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    val original = getClass.getResourceAsStream("/bird_small.png")
    val expected = getClass.getResourceAsStream("/bird_small_glow.png")

    test("filter output matches expected") {
        assert(Image(original).filter(GlowFilter()) === Image(expected))
    }
}
