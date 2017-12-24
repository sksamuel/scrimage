package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.filter.PointillizeGridType.{Hexagonal, Octangal, Square, Triangular}
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class PointillizeFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  private val original = Image(getClass.getResourceAsStream("/bird_small.png"))
  private val square = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_pointillize_square.png")
  private val hexagonal = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_pointillize_hexagon.png")
  private val octagonal = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_pointillize_octagonal.png")
  private val triangular = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_pointillize_triangular.png")

  test("filter output matches expected") {
    assert(original.filter(PointillizeFilter(Square)) === Image(square))
    assert(original.filter(PointillizeFilter(Hexagonal)) === Image(hexagonal))
    assert(original.filter(PointillizeFilter(Octangal)) === Image(octagonal))
    assert(original.filter(PointillizeFilter(Triangular)) === Image(triangular))
  }
}
