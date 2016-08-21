package com.sksamuel.scrimage.filter

import org.scalatest.{ OneInstancePerTest, BeforeAndAfter, FunSuite }
import com.sksamuel.scrimage.Image

/** @author Stephen Samuel */
class EmbossFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = Image.fromStream(getClass.getResourceAsStream("/bird_small.png"))

  test("emboss filter output matches expected") {
    val expected = Image.fromStream(getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_emboss.png"))
    assert(original.filter(EmbossFilter) === expected)
  }
}
