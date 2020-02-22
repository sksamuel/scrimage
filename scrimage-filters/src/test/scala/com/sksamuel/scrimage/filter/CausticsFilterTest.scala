package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PngWriter
import org.scalatest.{FunSuite, Matchers}

class CausticsFilterTest extends FunSuite with Matchers {

  implicit private val writer: PngWriter = PngWriter.MaxCompression
  private val original = ImmutableImage.fromResource("/bird_small.png")

  test("filter output matches expected") {
    val actual = original.filter(new CausticsFilter())
    val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_caustics.png")
    actual shouldBe expected
  }
}
