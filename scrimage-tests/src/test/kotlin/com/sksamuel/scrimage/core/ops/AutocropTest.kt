@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.ops

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AutocropTest : FunSpec({

   test("autocrop removes background with no tolerance") {
      val image = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/dyson.png")
      val autocropped = image.autocrop(java.awt.Color.WHITE)
      autocropped.width shouldBe 282
      autocropped.height shouldBe 193
      val expected = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/dyson_autocropped.png")
      autocropped shouldBe expected
   }

   test("autocrop should remove transparency") {
      val image = ImmutableImage.loader().fromResource("/balloon.png")
      val autocropped = image.autocrop()
      autocropped.width shouldBe 612
      autocropped.height shouldBe 965
      val expected = ImmutableImage.loader().fromResource("/balloon_autocropped.png")
      autocropped shouldBe expected
   }
})
