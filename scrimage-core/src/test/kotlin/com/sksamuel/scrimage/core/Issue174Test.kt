package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class Issue174Test : FunSpec({

   // https://github.com/sksamuel/scrimage/issues/174
   test("Unparsable GIF #174") {
      val image = ImmutableImage.fromResource("/github174.gif")
      image.width shouldBe 484
      image.height shouldBe 332
   }

})
