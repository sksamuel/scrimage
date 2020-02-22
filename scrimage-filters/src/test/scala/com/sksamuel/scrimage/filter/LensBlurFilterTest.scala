package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PngWriter
import org.scalatest.{FunSuite, Matchers}

class LensBlurFilterTest extends FunSuite with Matchers {

  implicit private val writer: PngWriter = PngWriter.MaxCompression
  private val original = ImmutableImage.fromResource("/bird_small.png")

  ignore("filter output matches expected") {
    val actual = original.copy.filter(new LensBlurFilter())
    val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_lens_blur.png")
    actual shouldBe expected
  }
}
