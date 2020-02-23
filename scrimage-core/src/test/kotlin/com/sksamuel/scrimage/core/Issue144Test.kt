package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

class Issue144Test : WordSpec({

   "Cover resize" should {
      "not produce a black bar on an image with an odd size" {
         val img = ImmutableImage.filled(2357, 2400, Color.WHITE)

         val resized = img.cover(200, 200)
         val pixel = resized.pixel(199, 0)

         pixel.red() shouldBe 255
         pixel.blue() shouldBe 255
         pixel.green() shouldBe 255
      }
   }

})