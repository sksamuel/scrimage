package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import org.scalatest.FunSuite

class TelevisionFilterTest extends FunSuite {

  private val original = Image.fromResource("/bird_small.png")
  private val expected = Image.fromResource("/com/sksamuel/scrimage/filters/bird_small_television.png")

  test("television filter output matches expected") {
    assert(original.filter(new TelevisionFilter) === expected)
  }
}
