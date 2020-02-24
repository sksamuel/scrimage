@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.ops

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AutocropTest : FunSpec({

   test("autocrop removes background with no tolerance") {
      val image = ImmutableImage.fromResource("/com/sksamuel/scrimage/dyson.png")
      val autocropped = image.autocrop(java.awt.Color.WHITE)
      282 shouldBe autocropped.width
      193 shouldBe autocropped.height
      val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/dyson_autocropped.png")
      assert(expected == autocropped)
   }
})
