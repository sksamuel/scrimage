package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.angles.Degrees
import io.kotest.core.datatest.forAll
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe

class AnglesTest : FunSpec() {
   init {

      data class DegreeConversion(val input: Int, val output: Double)

      context("degrees to rads") {
         forAll(
            DegreeConversion(45, 0.785398),
            DegreeConversion(180, 3.14159),
            DegreeConversion(360, 6.28319),
            DegreeConversion(89, 1.55334)
         ) { (deg, rad) ->
            Degrees(deg).toRadians().value shouldBe (rad plusOrMinus 0.01)
         }
      }
   }
}
