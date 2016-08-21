package com.sksamuel.scrimage.filter

import org.scalatest.{ OneInstancePerTest, BeforeAndAfter, FunSuite }
import com.sksamuel.scrimage.Image

/** @author Stephen Samuel */
class HsbFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = Image.fromStream(getClass.getResourceAsStream("/bird_small.png"))

  test("filter output matches expected") {
    val expected = Image.fromStream(getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_hue.png"))
    assert(original.filter(HSBFilter(0.5)) === expected)
  }
}
