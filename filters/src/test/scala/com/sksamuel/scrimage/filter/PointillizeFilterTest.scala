package com.sksamuel.scrimage.filter

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.filter.PointillizeGridType.{Triangular, Octangal, Hexagonal, Square}

/** @author Stephen Samuel */
class PointillizeFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    val original = Image(getClass.getResourceAsStream("/bird_small.png"))
    val square = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_pointillize_square.png")
    val hexagonal = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_pointillize_hexagon.png")
    val octagonal = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_pointillize_octagonal.png")
    val triangular = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_pointillize_triangular.png")

    test("filter output matches expected") {
        assert(original.filter(PointillizeFilter(Square)) === Image(square))
        assert(original.filter(PointillizeFilter(Hexagonal)) === Image(hexagonal))
        assert(original.filter(PointillizeFilter(Octangal)) === Image(octagonal))
        assert(original.filter(PointillizeFilter(Triangular)) === Image(triangular))
    }
}
