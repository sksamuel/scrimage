package com.sksamuel.scrimage.filter

import java.awt.image.BufferedImage

import com.sksamuel.scrimage.ImmutableImage
import org.scalatest.FunSuite

class SummerFilterTest extends FunSuite {

  private val original = ImmutableImage.fromResource("/bird_small.png", BufferedImage.TYPE_INT_ARGB)

  test("summer filter output matches expected") {
    val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_summer.png")
    val actual = original.filter(new SummerFilter(true))
    assert(actual === expected)
  }
}
