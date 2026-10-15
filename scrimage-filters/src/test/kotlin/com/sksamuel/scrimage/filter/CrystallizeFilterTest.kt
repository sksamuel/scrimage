package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

// Output-regression coverage for CrystallizeFilter, which is backed by the jhlabs
// CellularFilter. With randomness == 0 the cellular grid is fully deterministic
// (the internal Random is re-seeded per cube), so output can be pinned against
// golden images committed in test resources.
class CrystallizeFilterTest : FunSpec({

   val original = ImmutableImage.fromResource("/bird_small.png")

   test("filter output matches expected") {
      val actual = original.filter(CrystallizeFilter(16.0, 0.4, 0xff000000.toInt(), 0.0))
      val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_crystallize.png")
      actual shouldBe expected
   }

   test("support edge thickness") {
      val actual = original.filter(CrystallizeFilter(16.0, 0.6, 0xff000000.toInt(), 0.0))
      val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_crystallize_edge_thickeness.png")
      actual shouldBe expected
   }

   test("support edge colour") {
      val actual = original.filter(CrystallizeFilter(16.0, 0.6, 0xff00ff00.toInt(), 0.0))
      val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_crystallize_edge_colour.png")
      actual shouldBe expected
   }

   test("support scale") {
      val actual = original.filter(CrystallizeFilter(12.0, 0.4, 0xff000000.toInt(), 0.0))
      val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/bird_small_crystallize_scale.png")
      actual shouldBe expected
   }

   test("support randomness") {
      original.filter(CrystallizeFilter(16.0, 0.8, 0xff000000.toInt(), 0.2)) shouldNotBe
         original.filter(CrystallizeFilter(16.0, 0.8, 0xff000000.toInt(), 0.4))
   }

   // With randomness != 0 the jhlabs CellularFilter perturbs cell points via the
   // static Noise tables, which are seeded from an unseeded Random at class load,
   // so output cannot be pinned across JVMs. It must however be stable within a
   // JVM: two identically configured runs must be pixel-identical.
   test("randomness output is repeatable within the same JVM") {
      val a = original.filter(CrystallizeFilter(16.0, 0.4, 0xff000000.toInt(), 0.2))
      val b = original.filter(CrystallizeFilter(16.0, 0.4, 0xff000000.toInt(), 0.2))
      a shouldBe b
   }
})
