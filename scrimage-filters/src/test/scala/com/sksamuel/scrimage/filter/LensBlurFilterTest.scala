package com.sksamuel.scrimage.filter

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import com.sksamuel.scrimage.Image

/** @author Stephen Samuel */
class LensBlurFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = Image.fromResource("/bird_small.png")

  test("filter output matches expected") {
    assert(original.copy.filter(LensBlurFilter()) === Image.fromResource("/bird_lens_blur.png"))
  }
}
