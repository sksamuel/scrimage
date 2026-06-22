package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Covers the fadeout constructor parameter, passed through to the jhlabs
 * SmearFilter (setFadeout).
 *
 * Note: the jhlabs SmearFilter stores fadeout but never reads it in its
 * shape-drawing algorithm, so it currently has no observable effect. The value
 * is wired through for API completeness; this test only verifies it is accepted
 * and the filter applies cleanly.
 */
class SmearFilterFadeoutTest : FunSpec({

   val src = ImmutableImage.filled(40, 40, Color(120, 120, 120))

   test("fadeout is accepted and the filter applies") {
      val out = src.filter(SmearFilter(SmearType.Lines, 0f, 0.3f, 0f, 3, 0.4f, 10))
      out.width shouldBe 40
      out.height shouldBe 40
   }

   test("the six-arg constructor still applies") {
      src.filter(SmearFilter(SmearType.Lines, 0f, 0.3f, 0f, 3, 0.4f)).width shouldBe 40
   }
})
