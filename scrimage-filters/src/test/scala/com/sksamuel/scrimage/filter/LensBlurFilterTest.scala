package com.sksamuel.scrimage.filter

import org.scalatest.{ OneInstancePerTest, BeforeAndAfter, FunSuite }
import com.sksamuel.scrimage.Image

/** @author Stephen Samuel */
class LensBlurFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = Image(getClass.getResourceAsStream("/bird_small.png"))
  val expected = getClass.getResourceAsStream("/bird_lens_blur.png")

  test("filter output matches expected") {
    assert(Image(original).filter(LensBlurFilter()) === Image(expected))
  }
}
