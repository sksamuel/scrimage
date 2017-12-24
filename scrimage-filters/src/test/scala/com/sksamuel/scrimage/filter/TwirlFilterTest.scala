package com.sksamuel.scrimage.filter

import org.scalatest.{ OneInstancePerTest, BeforeAndAfter, FunSuite }
import com.sksamuel.scrimage.Image

class TwirlFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  private val original = Image(getClass.getResourceAsStream("/bird_small.png"))

  test("twirl filter output matches expected") {
    val expected = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_twirl.png")
    assert(original.filter(new TwirlFilter(150)) === Image(expected))
  }
}
