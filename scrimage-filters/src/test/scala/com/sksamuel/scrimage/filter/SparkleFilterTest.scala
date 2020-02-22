package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class SparkleFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  test("filter output matches expected") {
    assert(ImmutableImage.fromResource("/bird_small.png").filter(new SparkleFilter()) === ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_sparkle.png"))
  }
}
