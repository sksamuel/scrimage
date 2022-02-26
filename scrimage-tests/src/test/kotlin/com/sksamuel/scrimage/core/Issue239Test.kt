package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.MutableImage
import io.kotest.core.spec.style.FunSpec
import java.awt.image.BufferedImage

class Issue239Test : FunSpec({

   // https://github.com/sksamuel/scrimage/issues/239
   test("Pixels with alpha 0 turns black #239") {
      val image = ImmutableImage.loader().fromResource("/issue239.jpg")
      val mutImage = MutableImage(image.copy(BufferedImage.TYPE_INT_ARGB).awt())
      for (pixel in mutImage.pixels()) {
         if (pixel.red() > 240 && pixel.green() > 240 && pixel.blue() > 240) {
            val newPixel = pixel.fullalpha().red(125).blue(0).green(0)
            mutImage.setPixel(newPixel)
         }
      }

      // issue was that the source image did not have alpha enabled
   }

})
