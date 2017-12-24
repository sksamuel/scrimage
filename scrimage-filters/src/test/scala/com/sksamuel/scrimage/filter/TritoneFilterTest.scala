package com.sksamuel.scrimage.filter

import java.awt.Color

import com.sksamuel.scrimage.Image
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class TritoneFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  private val original = Image.fromResource("/bird_small.png")

  test("tritone filter output matches expected") {
    val expected = Image.fromResource("/com/sksamuel/scrimage/filters/bird_small_tritone.png")
    assert(original.filter(new TritoneFilter(new Color(0xFF000044), new Color(0xFF0066FF), Color.WHITE)) === expected)
  }
}
