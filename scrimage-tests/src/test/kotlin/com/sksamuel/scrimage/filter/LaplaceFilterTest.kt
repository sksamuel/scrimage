package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

class LaplaceFilterTest : FunSpec({

   test("applies to a single-row image without throwing") {
      // height == 1 means there is no row below, so the internal `row3` neighbour
      // buffer was never initialised; the inner x-loop (which runs for width >= 3)
      // then dereferenced a null row3 -> NullPointerException.
      for (width in listOf(3, 4, 10)) {
         val out = ImmutableImage.filled(width, 1, Color.WHITE).filter(LaplaceFilter())
         out.width shouldBe width
         out.height shouldBe 1
      }
   }

   test("applies to a single-column image without throwing") {
      val out = ImmutableImage.filled(1, 10, Color.WHITE).filter(LaplaceFilter())
      out.width shouldBe 1
      out.height shouldBe 10
   }
})
