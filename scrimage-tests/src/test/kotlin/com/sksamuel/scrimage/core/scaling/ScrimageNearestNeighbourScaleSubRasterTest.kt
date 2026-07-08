package com.sksamuel.scrimage.core.scaling

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.ScaleMethod
import com.sksamuel.scrimage.scaling.ScrimageNearestNeighbourScale
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage

/**
 * Regression: ScrimageNearestNeighbourScale read the source raster's backing
 * int[] assuming it maps 1:1 onto the image (offset 0, stride == width). A
 * sub-image view created with BufferedImage.getSubimage shares the parent's
 * buffer with a translated origin and the parent's scanline stride, but still
 * reports TYPE_INT_ARGB, so wrapAwt(subimage).scaleTo(w, h, FastScale) read
 * with the wrong stride/origin and produced wrong pixels.
 *
 * Sub-raster sources now delegate to the getRGB-based AwtNearestNeighbourScale.
 * Scale ratios in these tests are exact powers of two so both implementations
 * sample identical source pixels.
 */
class ScrimageNearestNeighbourScaleSubRasterTest : FunSpec({

   fun parentWithCoordinateColors(): BufferedImage {
      val parent = BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB)
      for (y in 0 until 8) {
         for (x in 0 until 8) {
            // encode the coordinates in the pixel value so misreads are obvious
            parent.setRGB(x, y, (0xFF shl 24) or (x shl 16) or (y shl 8) or 0x12)
         }
      }
      return parent
   }

   test("scale on a subimage source matches scale on a defensive copy of the view") {
      val parent = parentWithCoordinateColors()
      val sub = parent.getSubimage(2, 2, 4, 4)
      val copy = ImmutableImage.fromAwt(sub).awt()

      val fromView = ScrimageNearestNeighbourScale().scale(sub, 2, 2)
      val fromCopy = ScrimageNearestNeighbourScale().scale(copy, 2, 2)

      for (y in 0 until 2) {
         for (x in 0 until 2) {
            fromView.getRGB(x, y) shouldBe fromCopy.getRGB(x, y)
         }
      }
      // nearest neighbour with a 2:1 ratio samples view (0,0) = parent (2,2)
      fromView.getRGB(0, 0) shouldBe ((0xFF shl 24) or (2 shl 16) or (2 shl 8) or 0x12)
   }

   test("scaleTo with FastScale on a subimage-backed image matches a defensive copy") {
      val parent = parentWithCoordinateColors()
      val sub = parent.getSubimage(2, 2, 4, 4)

      val view = ImmutableImage.wrapAwt(sub)
      val copy = ImmutableImage.fromAwt(sub)

      val scaledView = view.scaleTo(2, 2, ScaleMethod.FastScale)
      val scaledCopy = copy.scaleTo(2, 2, ScaleMethod.FastScale)

      scaledView.pixels().map { it.argb } shouldBe scaledCopy.pixels().map { it.argb }
   }

   test("upscaling a subimage source matches upscaling a defensive copy") {
      val parent = parentWithCoordinateColors()
      val sub = parent.getSubimage(2, 2, 4, 4)
      val copy = ImmutableImage.fromAwt(sub).awt()

      val fromView = ScrimageNearestNeighbourScale().scale(sub, 8, 8)
      val fromCopy = ScrimageNearestNeighbourScale().scale(copy, 8, 8)

      for (y in 0 until 8) {
         for (x in 0 until 8) {
            fromView.getRGB(x, y) shouldBe fromCopy.getRGB(x, y)
         }
      }
   }
})
