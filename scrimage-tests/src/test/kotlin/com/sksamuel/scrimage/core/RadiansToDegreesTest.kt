package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.angles.Degrees
import com.sksamuel.scrimage.angles.Radians
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Regression test for Radians.toDegrees() previously using a (int) cast,
 * which truncates toward zero — losing up to 1 degree of precision when
 * the float result was just under an integer (e.g. 89.999... → 89, not 90).
 *
 * The fix uses Math.round to round to the nearest integer.
 */
class RadiansToDegreesTest : FunSpec({

   test("Degrees → Radians → Degrees round-trips for cardinal angles") {
      for (deg in listOf(0, 30, 45, 60, 90, 120, 180, 270, 360)) {
         Degrees(deg).toRadians().toDegrees().value shouldBe deg
      }
   }

   test("Degrees → Radians → Degrees round-trips for negative angles") {
      Degrees(-45).toRadians().toDegrees().value shouldBe -45
      Degrees(-90).toRadians().toDegrees().value shouldBe -90
      Degrees(-180).toRadians().toDegrees().value shouldBe -180
   }

   test("Degrees → Radians → Degrees round-trips for arbitrary integer angles") {
      for (deg in 1..359) {
         Degrees(deg).toRadians().toDegrees().value shouldBe deg
      }
   }

   test("Radians value just under an integer degree rounds up to that degree") {
      // 89.999° in radians; pre-fix the (int) cast truncated to 89.
      val almost90 = Radians(Math.toRadians(89.99999))
      almost90.toDegrees().value shouldBe 90
   }
})
