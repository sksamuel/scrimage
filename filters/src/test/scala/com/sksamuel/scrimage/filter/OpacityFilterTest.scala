package com.sksamuel.scrimage.filter

import java.io.File

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import com.sksamuel.scrimage.{Format, Image}

/** @author Stephen Samuel */
class OpacityFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = Image(getClass.getResourceAsStream("/bird_small.png"))

  test("opacity filter output matches expected") {
    val expected = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_opacity.png"))
    assert(actual === expected)
  }
}
