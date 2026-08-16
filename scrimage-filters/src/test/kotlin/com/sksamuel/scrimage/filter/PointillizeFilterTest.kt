package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

// Output-regression coverage for PointillizeFilter, which is backed by the jhlabs
// CellularFilter. PointillizeFilter uses randomness == 0, so every grid type
// (including the hexagonal/octagonal/triangular checkCube branches) is fully
// deterministic and can be pinned against golden images in test resources.
class PointillizeFilterTest : FunSpec({

   val original = ImmutableImage.fromResource("/bird_small.png")

   test("square grid output matches expected") {
      val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_pointillize_square.png")
      original.filter(PointillizeFilter(PointillizeGridType.Square)) shouldBe expected
   }

   test("hexagonal grid output matches expected") {
      val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_pointillize_hexagon.png")
      original.filter(PointillizeFilter(PointillizeGridType.Hexagonal)) shouldBe expected
   }

   test("octagonal grid output matches expected") {
      val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_pointillize_octagonal.png")
      original.filter(PointillizeFilter(PointillizeGridType.Octangal)) shouldBe expected
   }

   test("triangular grid output matches expected") {
      val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_pointillize_triangular.png")
      original.filter(PointillizeFilter(PointillizeGridType.Triangular)) shouldBe expected
   }

   test("should support scale") {
      val actual = original.filter(PointillizeFilter(0.0f, 12, 0.4f, 0xff000000.toInt(), false, 0.1f, PointillizeGridType.Hexagonal))
      val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_pointillize_hexagon_scale.png")
      actual shouldBe expected
   }

   test("should support edge thickness") {
      val actual = original.filter(PointillizeFilter(0.0f, 12, 0.45f, 0xff000000.toInt(), false, 0.1f, PointillizeGridType.Hexagonal))
      val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_pointillize_hexagon_edge_thickness.png")
      actual shouldBe expected
   }
})
