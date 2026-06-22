package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Verifies the fadeEdges constructor parameter is honoured and passed through to
 * the jhlabs CrystallizeFilter (fadeEdges blends cell edges instead of drawing
 * them in edgeColor, so the output differs).
 */
class CrystallizeFilterFadeEdgesTest : FunSpec({

   val original = ImmutableImage.fromResource("/bird_small.png")

   test("fadeEdges changes the output") {
      val faded = original.filter(CrystallizeFilter(16.0, 0.4, 0xff000000.toInt(), 0.0, true))
      val notFaded = original.filter(CrystallizeFilter(16.0, 0.4, 0xff000000.toInt(), 0.0, false))
      faded shouldNotBe notFaded
   }

   test("the four-arg constructor defaults fadeEdges to false") {
      val fourArg = original.filter(CrystallizeFilter(16.0, 0.4, 0xff000000.toInt(), 0.0))
      val explicitFalse = original.filter(CrystallizeFilter(16.0, 0.4, 0xff000000.toInt(), 0.0, false))
      fourArg shouldBe explicitFalse
   }
})
