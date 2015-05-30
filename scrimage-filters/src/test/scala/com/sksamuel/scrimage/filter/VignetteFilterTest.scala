package com.sksamuel.scrimage.filter

import java.io.File

import com.sksamuel.scrimage.Image
import org.scalatest.FunSuite

/** @author Stephen Samuel */
class VignetteFilterTest extends FunSuite {

  val original = Image(getClass.getResourceAsStream("/bird_small.png"))

  ignore("filter output matches expected") {
    val expected = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_vignette.png"))
    val actual = original.filter(VignetteFilter())
    actual.output(new File("vignette.png"))
    assert(actual === expected)
  }
}
