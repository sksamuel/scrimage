package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.nio.PngWriter
import org.scalatest.{FunSuite, Matchers}

class CausticsFilterTest extends FunSuite with Matchers {

  implicit private val writer: PngWriter = PngWriter.MaxCompression
  private val original = Image.fromResource("/bird_small.png")

  test("filter output matches expected") {
    val actual = original.filter(new CausticsFilter())
    val expected = Image.fromResource("/com/sksamuel/scrimage/filters/bird_small_caustics.png")
    actual shouldBe expected
  }
}
