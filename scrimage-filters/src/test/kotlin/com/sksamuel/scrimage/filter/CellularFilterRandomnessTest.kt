package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.awt.image.BufferedImageOp
import thirdparty.jhlabs.image.CellularFilter

// Regression coverage for the randomness != 0 branches of the jhlabs
// CellularFilter checkCube method, which perturb hexagonal/octagonal/triangular
// grid points using the static Noise.noise2 tables. Those tables are seeded from
// an unseeded Random at class load, so this output cannot be pinned to a golden
// image across JVMs — but within a JVM it must be fully repeatable: two
// identically configured runs over the same source must be pixel-identical.
class CellularFilterRandomnessTest : FunSpec({

   val original = ImmutableImage.fromResource("/bird_small.png")

   fun cellular(gridType: Int): Filter = object : BufferedOpFilter() {
      override fun op(): BufferedImageOp {
         val op = CellularFilter()
         op.gridType = gridType
         op.randomness = 0.3f
         op.scale = 16f
         return op
      }
   }

   val gridTypes = mapOf(
      "random" to CellularFilter.RANDOM,
      "square" to CellularFilter.SQUARE,
      "hexagonal" to CellularFilter.HEXAGONAL,
      "octagonal" to CellularFilter.OCTAGONAL,
      "triangular" to CellularFilter.TRIANGULAR,
   )

   for ((name, gridType) in gridTypes) {
      test("CellularFilter with randomness on $name grid is repeatable within the same JVM") {
         val a = original.filter(cellular(gridType))
         val b = original.filter(cellular(gridType))
         a shouldBe b
         a.width shouldBe original.width
         a.height shouldBe original.height
         a shouldNotBe original
      }
   }
})
