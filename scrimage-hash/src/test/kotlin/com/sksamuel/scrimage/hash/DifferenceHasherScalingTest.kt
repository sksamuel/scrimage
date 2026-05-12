package com.sksamuel.scrimage.hash

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.awt.Color
import java.awt.image.BufferedImage

class DifferenceHasherScalingTest : FunSpec({

   // Both images are 200x200; the centre 9x8 pixels (around col ~95..103,
   // row ~96..103) are uniformly white in both. The only difference is a
   // big black block painted in the top-left corner of `withCornerBlock`.
   //
   // A perceptual hash that actually scales the image down should produce
   // different hashes for these two images, because the corner block shows
   // up as a darker pixel in the scaled-down representation. The previous
   // implementation called resizeTo (a canvas-resize that crops, not a
   // resampling op), so for any image larger than (cols x rows) it just
   // captured the centre region — which is identical here — and yielded
   // the same hash for both images.

   fun fill(width: Int, height: Int, paint: (BufferedImage) -> Unit): ImmutableImage {
      val bi = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
      val g = bi.createGraphics()
      try {
         g.color = Color.WHITE
         g.fillRect(0, 0, width, height)
         paint(bi)
      } finally {
         g.dispose()
      }
      return ImmutableImage.fromAwt(bi)
   }

   test("dhash of two images with identical centres but different corners differs") {
      val plain = fill(200, 200) { }
      val withCornerBlock = fill(200, 200) {
         val g = it.createGraphics()
         try {
            g.color = Color.BLACK
            g.fillRect(0, 0, 80, 80)
         } finally {
            g.dispose()
         }
      }

      // Sanity: the two images really are different overall.
      plain.argb(0, 0).contentEquals(withCornerBlock.argb(0, 0)) shouldBe false

      // Sanity: the centre region of each image is uniformly white, so a
      // pure centre-crop would not be able to tell them apart.
      val centreColumns = 95..103
      val centreRows = 96..103
      for (x in centreColumns) for (y in centreRows) {
         plain.pixel(x, y).toARGBInt() shouldBe withCornerBlock.pixel(x, y).toARGBInt()
      }

      // The hash must summarise the whole image, not just the centre.
      plain.dhash() shouldNotBe withCornerBlock.dhash()
   }
})
