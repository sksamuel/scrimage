package com.sksamuel.scrimage.filter

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.filter.PointillizeGridType.{Hexagonal, Square}

/** @author Stephen Samuel */
class PointillizeFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    val original = Image(getClass.getResourceAsStream("/bird_small.png"))
    val square = getClass.getResourceAsStream("/bird_small_pointillize_square.png")
    val hexagonal = getClass.getResourceAsStream("/bird_small_pointillize_hexagon.png")

    test("filter output matches expected") {
        assert(original.filter(PointillizeFilter(Square)) === Image(square))
        assert(original.filter(PointillizeFilter(Hexagonal)) === Image(hexagonal))
    }
}
