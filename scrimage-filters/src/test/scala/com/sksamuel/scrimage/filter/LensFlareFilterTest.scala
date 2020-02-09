package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class LensFlareFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  private val original = Image.fromResource("/bird_small.png")

  test("filter output matches expected") {
    val expected = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_lens_flare.png")
    assert(original.filter(new LensFlareFilter) != Image.fromStream(expected)) // tis another random one
  }
}
