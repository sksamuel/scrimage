package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.nio.PngWriter
import org.scalatest.{FunSuite, Matchers}

class OilFilterTest extends FunSuite with Matchers {

  implicit private val writer: PngWriter = PngWriter.MaxCompression

  private val original = Image.fromResource("/bird_small.png")
  private val expected = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_oil.png")

  test("filter output matches expected") {
    original.filter(new OilFilter()) shouldBe Image.fromStream(expected)
  }

  test("support ranges") {
    original.filter(new OilFilter(6, 4)) shouldBe Image.fromResource("/com/sksamuel/scrimage/filters/bird_small_oil_6_4.png")
    original.filter(new OilFilter(8, 4)) shouldBe Image.fromResource("/com/sksamuel/scrimage/filters/bird_small_oil_8_4.png")
    original.filter(new OilFilter(10, 4)) shouldBe Image.fromResource("/com/sksamuel/scrimage/filters/bird_small_oil_10_4.png")
    original.filter(new OilFilter(12, 4)) shouldBe Image.fromResource("/com/sksamuel/scrimage/filters/bird_small_oil_12_4.png")
  }

  test("support levels") {
    original.filter(new OilFilter(6, 4)) shouldBe Image.fromResource("/com/sksamuel/scrimage/filters/bird_small_oil_6_4.png")
    original.filter(new OilFilter(6, 12)) shouldBe Image.fromResource("/com/sksamuel/scrimage/filters/bird_small_oil_6_12.png")
    original.filter(new OilFilter(6, 20)) shouldBe Image.fromResource("/com/sksamuel/scrimage/filters/bird_small_oil_6_20.png")
    original.filter(new OilFilter(6, 28)) shouldBe Image.fromResource("/com/sksamuel/scrimage/filters/bird_small_oil_6_28.png")
  }
}
