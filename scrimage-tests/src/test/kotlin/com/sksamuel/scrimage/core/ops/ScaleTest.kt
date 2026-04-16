@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.ops

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.ScaleMethod
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.shouldBe

class ScaleTest : FunSpec({

   val image = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/bird.jpg")

   test("Bicubic scale test") {
      image.scaleTo(486, 324) shouldBe ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/scale/bird_486_324_bicubic.png")
   }

   test("Bilinear scale test") {
      image.scaleTo(486, 324, ScaleMethod.Bilinear) shouldBe ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/scale/bird_486_324_bilinear.png")
   }

   test("Progressive bilinear scale down test") {
      image.scaleTo(486, 324, ScaleMethod.Progressive) shouldBe ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/scale/bird_486_324_prog_bilinear.png")
   }

   test("Progressive bilinear scale up test should complete") {
      image.scaleTo(3000, 3000, ScaleMethod.Progressive).width shouldBe 3000
      image.scaleTo(3000, 100, ScaleMethod.Progressive).height shouldBe 100
      image.scaleTo(100, 3000, ScaleMethod.Progressive).width shouldBe 100
   }

   test("when scaling an image the output image should match as expected") {
      val scaled = image.scale(0.25)
      val expected = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/bird_scale_025.png")
      expected.width shouldBe scaled.width
      expected.height shouldBe scaled.height
   }

   test("when scaling an image the output image should have specified dimensions") {
      val scaled = image.scaleTo(900, 300)
      scaled.width shouldBe 900
      scaled.height shouldBe 300
   }

   test("when scaling by width then target image maintains aspect ratio") {
      val scaled = image.scaleToWidth(500)
      scaled.width shouldBe 500
      scaled.ratio() - image.ratio() shouldBeLessThan 0.01
   }

   test("when scaling by height then target image maintains aspect ratio") {
      val scaled = image.scaleToHeight(400)
      scaled.height shouldBe 400
      scaled.ratio() - image.ratio() shouldBeLessThan 0.01
   }

   // Regression test for https://github.com/sksamuel/scrimage/pull/353
   // ScrimageNearestNeighbourScale reset k to the wrong row after each output row,
   // causing every row after the first to sample from the wrong source row.
   test("FastScale samples correct source rows when scaling down") {
      // Build a 4x4 TYPE_INT_ARGB image with four distinctly coloured rows.
      val pixels = Array(16) { i ->
         val x = i % 4
         val y = i / 4
         when (y) {
            0 -> Pixel(x, y, 255, 0, 0, 255)   // row 0: red
            1 -> Pixel(x, y, 0, 255, 0, 255)   // row 1: green
            2 -> Pixel(x, y, 0, 0, 255, 255)   // row 2: blue
            else -> Pixel(x, y, 255, 255, 255, 255) // row 3: white
         }
      }
      val src = ImmutableImage.create(4, 4, pixels)

      // FastScale uses ScrimageNearestNeighbourScale when both dimensions shrink
      // and the image type is TYPE_INT_ARGB (the default).
      // With yr = 4/2 = 2.0: output row 0 → source row 0 (red),
      //                       output row 1 → source row 2 (blue).
      val scaled = src.scaleTo(2, 2, ScaleMethod.FastScale)

      scaled.width shouldBe 2
      scaled.height shouldBe 2

      // output row 0 must be red (source row 0)
      scaled.pixel(0, 0).red() shouldBe 255
      scaled.pixel(0, 0).green() shouldBe 0
      scaled.pixel(0, 0).blue() shouldBe 0

      // output row 1 must be blue (source row 2), not red (source row 0)
      scaled.pixel(0, 1).red() shouldBe 0
      scaled.pixel(0, 1).green() shouldBe 0
      scaled.pixel(0, 1).blue() shouldBe 255
   }

})
