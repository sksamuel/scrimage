package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Covers the softness constructor parameter, passed through to the jhlabs
 * DissolveFilter (setSoftness). The dissolve is randomised, so this only checks
 * the parameter is accepted and the filter applies cleanly.
 */
class DissolveFilterSoftnessTest : FunSpec({

   val src = ImmutableImage.filled(40, 40, Color(120, 120, 120))

   test("softness is accepted and the filter applies") {
      val out = src.filter(DissolveFilter(0.5f, 0.7f))
      out.width shouldBe 40
      out.height shouldBe 40
   }

   test("the density-only constructor still applies") {
      src.filter(DissolveFilter(0.5f)).width shouldBe 40
   }
})
