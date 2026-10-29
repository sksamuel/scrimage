package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * filled() replicates its first row into every other row for non-int rasters;
 * every pixel (and every raw row) must equal the first one for every image type.
 */
class FilledAllTypesTest : FunSpec({
   for (type in 1..13) {
      test("filled fills every pixel identically for type $type") {
         val color = Color(10, 200, 30, 128)
         val image = ImmutableImage.filled(37, 11, color, type)
         val raster = image.awt().raster
         val firstRow = raster.getDataElements(0, 0, 37, 1, null)
         val expected = image.awt().colorModel.getDataElements(color.rgb, null)
         for (x in 0 until 37) {
            image.awt().getRGB(x, 0) shouldBe image.awt().getRGB(0, 0)
         }
         // first row holds exactly the colour model's data elements for the colour
         image.awt().getRGB(0, 0) shouldBe image.awt().colorModel.getRGB(expected)
         for (y in 1 until 11) {
            val row = raster.getDataElements(0, y, 37, 1, null)
            java.util.Objects.deepEquals(row, firstRow) shouldBe true
         }
      }
   }
})
