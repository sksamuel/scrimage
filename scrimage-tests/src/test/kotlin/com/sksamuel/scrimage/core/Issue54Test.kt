package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class Issue54Test : FunSpec({

   test("image for issue 54 should load as expected") {
      ImmutableImage.loader().fromResource("/issue54.jpg").width shouldBe 2560
      ImmutableImage.loader().fromResource("/issue54.jpg").scaleToWidth(400).width shouldBe 400
   }

})
