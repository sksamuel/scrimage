package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.nio.PngWriter
import org.scalatest.{FunSuite, Matchers}

class RippleFilterTest extends FunSuite with Matchers {

  implicit private val writer: PngWriter = PngWriter.MaxCompression
  private val original = Image.fromResource("/bird_small.png")

  ignore("support triangle") {
    val filter = new RippleFilter(RippleType.Triangle)
    val actual = original.filter(filter)
    val expected = Image.fromResource("/bird_ripple_triangle.png")
    actual shouldBe expected
  }

  ignore("support sawtooth") {
    val filter = new RippleFilter(RippleType.Sawtooth)
    val actual = original.filter(filter)
    val expected = Image.fromResource("/bird_ripple_sawtooth.png")
    actual shouldBe expected
  }
}
