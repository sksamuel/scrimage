package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.awt.Color

/**
 * Verifies the levels constructor parameter is passed through to the jhlabs
 * DitherFilter (setLevels), changing the number of dither levels per channel.
 */
class DitherFilterLevelsTest : FunSpec({

   val src = ImmutableImage.create(48, 48).map { p -> Color(p.x() * 5 % 256, p.y() * 5 % 256, 128) }

   test("levels is passed through") {
      val few = src.filter(DitherFilter(2))
      val many = src.filter(DitherFilter(16))
      few shouldNotBe many
   }

   test("the no-arg constructor still applies") {
      src.filter(DitherFilter()).width shouldBe 48
   }
})
