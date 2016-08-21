package com.sksamuel.scrimage.filter

import org.scalatest.{ OneInstancePerTest, BeforeAndAfter, FunSuite }
import com.sksamuel.scrimage.Image
import java.awt.Color

/** @author Stephen Samuel */
class TritoneFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = Image.fromStream(getClass.getResourceAsStream("/bird_small.png"))

  test("tritone filter output matches expected") {
    val expected = Image.fromStream(getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_tritone.png"))
    assert(original.filter(TritoneFilter(new Color(0xFF000044), new Color(0xFF0066FF), Color.WHITE)) === expected)
  }
}
