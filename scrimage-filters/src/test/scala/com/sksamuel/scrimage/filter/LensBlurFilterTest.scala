package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.nio.PngWriter
import org.scalatest.{FunSuite, Matchers}

class LensBlurFilterTest extends FunSuite with Matchers {

  implicit private val writer: PngWriter = PngWriter.MaxCompression
  private val original = Image.fromResource("/bird_small.png")

  ignore("filter output matches expected") {
    val actual = original.copy.filter(new LensBlurFilter())
    val expected = Image.fromResource("/com/sksamuel/scrimage/filters/bird_lens_blur.png")
    actual shouldBe expected
  }
}
