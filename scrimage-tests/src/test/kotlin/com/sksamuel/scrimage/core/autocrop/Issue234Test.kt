package com.sksamuel.scrimage.core.autocrop

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

class Issue234Test : FunSpec() {
   init {
      test("Autocrop not working properly when on small images #234") {
         val input = ImmutableImage.loader().fromResource("/issue234.png")
         input.autocrop(Color.green).dimensions() shouldBe input.dimensions()
      }
   }
}
