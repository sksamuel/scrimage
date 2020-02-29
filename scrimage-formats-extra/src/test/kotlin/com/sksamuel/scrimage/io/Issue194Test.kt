package com.sksamuel.scrimage.io

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec

class Issue194Test : FunSpec({

   test("Cannot transform some tiff images #194") {
      ImmutableImage.loader().fromResource("/issue194.tiff").cover(10, 10)
   }
})
