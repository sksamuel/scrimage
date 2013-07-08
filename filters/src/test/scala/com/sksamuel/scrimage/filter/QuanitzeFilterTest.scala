package com.sksamuel.scrimage.filter

import org.scalatest.{ OneInstancePerTest, BeforeAndAfter, FunSuite }
import com.sksamuel.scrimage.Image

/** @author Stephen Samuel */
class QuanitzeFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = getClass.getResourceAsStream("/bird_small.png")
  val expected_64 = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_quantize_64.png")
  val expected_256 = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_quantize_256.png")

  test("filter output matches expected") {
    val image = Image(original)
    assert(image.filter(QuantizeFilter(64)) == Image(expected_64))
    assert(image.filter(QuantizeFilter(256)) == Image(expected_256))
  }
}
