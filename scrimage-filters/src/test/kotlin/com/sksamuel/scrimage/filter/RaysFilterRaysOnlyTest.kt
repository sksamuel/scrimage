package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Covers the raysOnly constructor parameter, which is passed through to
 * thirdparty.jhlabs.image.RaysFilter.setRaysOnly.
 *
 * Note: raysOnly has no observable effect under the current BufferedOpFilter
 * invocation, which calls op().filter(img, img) with the same image as src and
 * dst. In jhlabs RaysFilter the flag only controls whether the source is drawn
 * onto dst before the rays are added; since dst IS the source here, that draw is
 * a no-op either way. These tests therefore only verify the flag is accepted and
 * the filter applies cleanly.
 */
class RaysFilterRaysOnlyTest : FunSpec({

   val src = ImmutableImage.create(100, 100).map { p ->
      val dx = p.x() - 50; val dy = p.y() - 50
      if (dx * dx + dy * dy < 10 * 10) Color(255, 255, 255) else Color.BLACK
   }

   test("raysOnly = true is accepted and the filter applies") {
      val out = src.filter(RaysFilter(1.0f, 0f, 0.5f, 0f, 0f, 0.35f, 0f, true))
      out.width shouldBe 100
      out.height shouldBe 100
   }

   test("the seven-arg constructor defaults raysOnly to false") {
      val sevenArg = src.filter(RaysFilter(1.0f, 0f, 0.5f, 0f, 0f, 0.35f, 0f))
      val explicitFalse = src.filter(RaysFilter(1.0f, 0f, 0.5f, 0f, 0f, 0.35f, 0f, false))
      sevenArg shouldBe explicitFalse
   }
})
