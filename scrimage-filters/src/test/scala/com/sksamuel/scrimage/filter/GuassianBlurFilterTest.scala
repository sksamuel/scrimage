package com.sksamuel.scrimage.filter

import org.scalatest.{ OneInstancePerTest, BeforeAndAfter, FunSuite }
import com.sksamuel.scrimage.Image

class GuassianBlurFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  private val original = Image.fromResource("/bird_small.png")

  test("filter output matches expected") {
    //original.filter(GaussianBlurFilter(6)).write(new File("src/test/resources/com/sksamuel/scrimage/filters/bird_small_guassian.png"))
    val expected = Image.fromResource("/com/sksamuel/scrimage/filters/bird_small_guassian.png")
    assert(original.filter(new GaussianBlurFilter(6)) === expected)
  }
}
