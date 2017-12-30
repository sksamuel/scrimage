package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import org.scalatest.{ BeforeAndAfter, FunSuite, OneInstancePerTest }

class GrayscaleFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = Image(getClass.getResourceAsStream("/bird_small.png"))

  test("filter output matches expected") {
    val expected = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_grayscale.png"))
    val actual = original.filter(new GrayscaleFilter)
    assert(actual === expected)
  }
}
