@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.io

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TwelveMonkeysFormatsTest : FunSpec({

   test("should support tiff images") {
      ImmutableImage.fromResource("/MARBLES.TIF").width shouldBe 1419
   }

   test("should support pcx images") {
      ImmutableImage.fromResource("/BLOOD73.PCX").width shouldBe 640
      ImmutableImage.fromResource("/dog.pcx").width shouldBe 2184
   }

   test("should support sgi images") {
      ImmutableImage.fromResource("/MARBLES.SGI").width shouldBe 1419
   }

   test("should support bmp images") {
      ImmutableImage.fromResource("/MARBLES.BMP").width shouldBe 1419
   }
})
