package com.sksamuel.scrimage.filter

import org.scalatest.{ OneInstancePerTest, BeforeAndAfter, FunSuite }
import com.sksamuel.scrimage.Image
import java.io.File

/** @author Stephen Samuel */
class DitherFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = Image.fromStream(getClass.getResourceAsStream("/bird_small.png"))

  test("filter output matches expected") {
    val expected = Image.fromStream(getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_dither.png"))
    assert(original.filter(DitherFilter) === expected)
  }
}
