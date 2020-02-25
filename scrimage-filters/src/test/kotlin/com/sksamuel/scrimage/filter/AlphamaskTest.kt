@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AlphamaskTest : FunSpec({

   val image = ImmutableImage.loader().fromResource("/tiger.jpg")
   val mask = ImmutableImage.loader().fromResource("/gradation.jpg")
   val expected = ImmutableImage.loader().fromResource("/alphamask.png")

   test("alphamask happy path") {
      image.cover(512, 256)
         .filter(AlphaMaskFilter(mask, 3)) shouldBe expected
   }
})
