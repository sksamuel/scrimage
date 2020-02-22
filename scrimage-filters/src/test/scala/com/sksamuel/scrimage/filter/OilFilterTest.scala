package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PngWriter
import org.scalatest.{FunSuite, Matchers}

class OilFilterTest extends FunSuite with Matchers {

  implicit private val writer: PngWriter = PngWriter.MaxCompression

  private val original = ImmutableImage.fromResource("/bird_small.png")
  private val expected = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_oil.png")

  test("filter output matches expected") {
    original.filter(new OilFilter()) shouldBe ImmutableImage.fromStream(expected)
  }

  test("support ranges") {
    original.filter(new OilFilter(6, 4)) shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_oil_6_4.png")
    original.filter(new OilFilter(8, 4)) shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_oil_8_4.png")
    original.filter(new OilFilter(10, 4)) shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_oil_10_4.png")
    original.filter(new OilFilter(12, 4)) shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_oil_12_4.png")
  }

  test("support levels") {
    original.filter(new OilFilter(6, 4)) shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_oil_6_4.png")
    original.filter(new OilFilter(6, 12)) shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_oil_6_12.png")
    original.filter(new OilFilter(6, 20)) shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_oil_6_20.png")
    original.filter(new OilFilter(6, 28)) shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_oil_6_28.png")
  }
}
