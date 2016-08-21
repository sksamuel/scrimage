package com.sksamuel.scrimage.filter

import org.scalatest.{ OneInstancePerTest, BeforeAndAfter, FunSuite }
import com.sksamuel.scrimage.Image

/** @author Stephen Samuel */
class MotionBlurFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = Image.fromStream(getClass.getResourceAsStream("/bird_small.png"))

  test("motion blur filter output matches expected") {
    val expected = Image.fromStream(getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_motionblur.png"))
    assert(original.filter(MotionBlurFilter(Math.PI / 3.0, 25)) === expected)
  }
}
