package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.awt.image.BufferedImage

/**
 * Regression test for the Floyd-Steinberg error-diffusion row bug shared by
 * jhlabs DiffusionFilter and QuantizeFilter: the write index was built from the
 * column offset only (index + j), never adding the row term (i * width), so the
 * error destined for the row below was applied to the current row instead.
 */
class DiffusionFilterTest : FunSpec() {
   init {
      test("error diffuses to the row below, not the current row") {
         // A 1x2 column of mid grey (127) at 2 levels quantizes the top pixel to black,
         // leaving an error of 127 that the Floyd-Steinberg kernel sends straight down
         // (weight 5/16 -> +39). The bottom pixel becomes 166 -> quantizes to white.
         // With the bug the error stayed on the top row, so the bottom pixel stayed 127
         // and quantized to black.
         val img = ImmutableImage.filled(1, 2, Color(127, 127, 127))
         val filter = thirdparty.jhlabs.image.DiffusionFilter()
         filter.setLevels(2)
         val out = BufferedImage(1, 2, BufferedImage.TYPE_INT_ARGB)
         filter.filter(img.awt(), out)

         (out.getRGB(0, 0) and 0xffffff) shouldBe 0x000000 // top: black
         (out.getRGB(0, 1) and 0xffffff) shouldBe 0xffffff // bottom: white
      }
   }
}
