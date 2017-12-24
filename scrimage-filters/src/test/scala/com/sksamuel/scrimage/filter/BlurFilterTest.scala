package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class BlurFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  test("filter output matches expected") {
    assert(Image.fromResource("/bird_small.png").filter(new BlurFilter) == Image.fromResource("/com/sksamuel/scrimage/filters/bird_small_blur.png"))
  }
}
