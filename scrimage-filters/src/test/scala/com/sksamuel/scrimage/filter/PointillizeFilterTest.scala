package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import org.scalatest.FunSuite

class PointillizeFilterTest extends FunSuite {

  private val original = Image(getClass.getResourceAsStream("/bird_small.png"))
  private val square = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_pointillize_square.png")
  private val hexagonal = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_pointillize_hexagon.png")
  private val octagonal = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_pointillize_octagonal.png")
  private val triangular = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_pointillize_triangular.png")

  test("filter output matches expected") {
    assert(original.filter(new PointillizeFilter(PointillizeGridType.Square)) === Image(square))
    assert(original.filter(new PointillizeFilter(PointillizeGridType.Hexagonal)) === Image(hexagonal))
    assert(original.filter(new PointillizeFilter(PointillizeGridType.Octangal)) === Image(octagonal))
    assert(original.filter(new PointillizeFilter(PointillizeGridType.Triangular)) === Image(triangular))
  }
}
