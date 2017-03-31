package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import org.scalatest.FunSuite

class BlackThresholdFilterTest extends FunSuite {

  test("black threshold filter output matches expected") {
    // given
    val image = Image.fromStream(getClass.getResourceAsStream("/bird_small.png"))
    val thresholdPercentage = 40.0

    // when
    BlackThresholdFilter(thresholdPercentage).apply(image)

    // then
    val expected = Image.fromStream(getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_black_threshold.png"))
    assert(image === expected)
  }
}
