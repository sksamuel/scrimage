package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class BrightnessFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = ImmutableImage.fromStream(getClass.getResourceAsStream("/bird_small.png"))

  test("filter output matches expected") {
    val expected = ImmutableImage.fromStream(getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_brighten.png"))
    assert(original.filter(new BrightnessFilter(1.4f)) === expected)
  }
}
