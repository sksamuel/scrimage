package com.sksamuel.scrimage

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Verifies the per-channel "fuzziness" tolerance of autocrop: a border colour
 * that drifts from the base colour by up to the tolerance (per channel) is
 * still cropped.
 */
class AutocropFuzzinessTest : FunSpec({

   // 6x6 image: a 1px border of [border], a different interior.
   fun bordered(border: Color, interior: Color): ImmutableImage =
      ImmutableImage.create(6, 6).map { p ->
         if (p.x == 0 || p.y == 0 || p.x == 5 || p.y == 5) border else interior
      }

   test("zero tolerance does not crop a border that is near but not exactly the base colour") {
      val img = bordered(Color(250, 250, 250), Color(0, 0, 255))
      val out = img.autocrop(Color.WHITE, 0)
      out.width shouldBe 6
      out.height shouldBe 6
   }

   test("a per-channel drift within the tolerance is cropped") {
      // border is 250 on every channel; base is 255; drift of 5 per channel
      val img = bordered(Color(250, 250, 250), Color(0, 0, 255))
      val out = img.autocrop(Color.WHITE, 5)
      out.width shouldBe 4
      out.height shouldBe 4
   }

   test("a per-channel drift larger than the tolerance is not cropped") {
      val img = bordered(Color(250, 250, 250), Color(0, 0, 255))
      val out = img.autocrop(Color.WHITE, 4) // drift is 5, tolerance only 4
      out.width shouldBe 6
      out.height shouldBe 6
   }

   test("tolerance of 1 treats 254 as matching a base of 255") {
      val img = bordered(Color(254, 254, 254), Color(0, 0, 0))
      img.autocrop(Color.WHITE, 0).width shouldBe 6   // 254 != 255 exactly
      img.autocrop(Color.WHITE, 1).width shouldBe 4   // 254 within 255 +/- 1
   }

   test("autocrop(tolerance) delegates to the default transparent background") {
      // border fully transparent, interior opaque red
      val img = ImmutableImage.create(6, 6).map { p ->
         if (p.x == 0 || p.y == 0 || p.x == 5 || p.y == 5) Color(0, 0, 0, 0) else Color(255, 0, 0, 255)
      }
      val out = img.autocrop(0)
      out.width shouldBe 4
      out.height shouldBe 4
   }
})
