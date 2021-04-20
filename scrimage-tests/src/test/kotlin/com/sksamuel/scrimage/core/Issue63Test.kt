package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class Issue63Test : FunSpec({

   test("image for issue 63 should parse") {
      ImmutableImage.fromStream(javaClass.getResourceAsStream("/issue63.png")).width shouldBe 1600
   }

})