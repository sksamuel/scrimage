package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.awt.Color

/**
 * Verifies the offset constructor parameter is passed through to the jhlabs
 * ContourFilter (setOffset), changing the rendered contours.
 */
class ContourFilterOffsetTest : FunSpec({

   val src = ImmutableImage.create(64, 64).map { p -> Color((p.x() * 4) % 256, (p.y() * 4) % 256, 100) }

   test("offset is passed through") {
      val a = src.filter(ContourFilter(3, 0f))
      val b = src.filter(ContourFilter(3, 0.5f))
      a shouldNotBe b
   }

   test("the levels-only constructor still applies") {
      src.filter(ContourFilter(4)).width shouldBe 64
   }
})
