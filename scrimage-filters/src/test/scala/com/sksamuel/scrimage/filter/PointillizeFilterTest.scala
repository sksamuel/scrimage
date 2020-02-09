package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.nio.PngWriter
import org.scalatest.{FunSuite, Matchers}

class PointillizeFilterTest extends FunSuite with Matchers {

  implicit private val writer: PngWriter = PngWriter.MaxCompression

  private val original = Image.fromStream(getClass.getResourceAsStream("/bird_small.png"))
  private val square = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_pointillize_square.png")
  private val hexagonal = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_pointillize_hexagon.png")
  private val octagonal = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_pointillize_octagonal.png")
  private val triangular = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_pointillize_triangular.png")

  test("filter output matches expected") {
    assert(original.filter(new PointillizeFilter(PointillizeGridType.Square)) === Image.fromStream(square))
    assert(original.filter(new PointillizeFilter(PointillizeGridType.Hexagonal)) === Image.fromStream(hexagonal))
    assert(original.filter(new PointillizeFilter(PointillizeGridType.Octangal)) === Image.fromStream(octagonal))
    assert(original.filter(new PointillizeFilter(PointillizeGridType.Triangular)) === Image.fromStream(triangular))
  }

  test("should support scale") {
    val actual = original.filter(new PointillizeFilter(0.0f, 12, 0.4f, 0xff000000, false, 0.1f, PointillizeGridType.Hexagonal))
    val expected = Image.fromResource("/com/sksamuel/scrimage/filters/bird_small_pointillize_hexagon_scale.png")
    actual shouldBe expected
  }

  test("should support edge thickness") {
    val actual = original.filter(new PointillizeFilter(0.0f, 12, 0.45f, 0xff000000, false, 0.1f, PointillizeGridType.Hexagonal))
    val expected = Image.fromResource("/com/sksamuel/scrimage/filters/bird_small_pointillize_hexagon_edge_thickness.png")
    actual shouldBe expected
  }
}
