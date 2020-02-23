package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec

class Issue29Test : FunSpec({

   test("image for issue 29 should load") {
      assert(ImmutableImage.fromStream(javaClass.getResourceAsStream("/issue29.jpeg")) != null)
   }
})