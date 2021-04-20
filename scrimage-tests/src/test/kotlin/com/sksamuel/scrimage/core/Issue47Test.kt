package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class Issue47Test : WordSpec({

   "loading iphone image" should {
      "detect rotation flag"  {
         val src = ImmutableImage.fromResource("/issue47.JPG")
         src.height shouldBe 3264
         src.width shouldBe 2448
      }
   }

})