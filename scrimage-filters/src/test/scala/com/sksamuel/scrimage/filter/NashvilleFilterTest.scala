package com.sksamuel.scrimage.filter

import java.io.File

import com.sksamuel.scrimage.Image
import org.scalatest.FunSuite

/** @author Stephen Samuel */
class NashvilleFilterTest extends FunSuite {

  val original = Image(getClass.getResourceAsStream("/bird_small.png"))

  ignore("filter output matches expected") {
    val expected = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_nashville.png")
    val actual = original.filter(NashvilleFilter)
    actual.output(new File("nashville.png"))
    assert(actual === Image(expected))
  }
}
