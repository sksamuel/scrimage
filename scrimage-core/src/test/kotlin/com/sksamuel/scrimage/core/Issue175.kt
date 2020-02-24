package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class Issue175 : FunSpec({

   val image = ImmutableImage.fromResource("/com/sksamuel/scrimage/bird.jpg")

   test("Copied image has a slightly different set of pixel values than original image #175") {
      val b = image.map { p -> p.toColor().withRed(0).awt() }
      val c = b.copy()
      b.pixels() shouldBe c.pixels()
   }
})
