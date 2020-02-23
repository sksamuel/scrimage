package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class Issue54Test : FunSpec({

   test("image for issue 54 should load as expected") {
      ImmutableImage.fromStream(this.javaClass.getResourceAsStream("/issue54.jpg")).width shouldBe 2560
      ImmutableImage.fromStream(this.javaClass.getResourceAsStream("/issue54.jpg")).scaleToWidth(400).pixels().toList() shouldBe
          ImmutableImage.fromStream(this.javaClass.getResourceAsStream("/issue54.png")).pixels().toList()
   }

})