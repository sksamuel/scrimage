package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class EmbossFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  private val original = ImmutableImage.fromResource("/bird_small.png")

  test("emboss filter output matches expected") {
    val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_emboss.png")
    assert(original.filter(new EmbossFilter) === expected)
  }
}
