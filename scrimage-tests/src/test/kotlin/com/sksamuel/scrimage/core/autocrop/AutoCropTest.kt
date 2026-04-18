package com.sksamuel.scrimage.core.autocrop

import com.sksamuel.scrimage.Dimension
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.ImmutableImageLoader
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.awt.Color

class AutoCropTest : FunSpec() {
   init {

      test("autocrop should work with transparent backgrounds") {
         val image = ImmutableImageLoader.create()
            .fromResource("/com/sksamuel/scrimage/core/autocrop/1b64a74b-ae2b-417e-a7db-b238e8ec5555.png")
         image.autocrop().dimensions() shouldBe Dimension(745, 1869)
      }

      test("autocrop with no matching content should return the same image") {
         val image = ImmutableImageLoader.create()
            .fromResource("/com/sksamuel/scrimage/core/autocrop/1b64a74b-ae2b-417e-a7db-b238e8ec5555.png")
         image.autocrop(Color.ORANGE).shouldBeSameInstanceAs(image)
      }

      // Regression: autocrop used subimage(x1, y1, x2-x1, y2-y1) which is off by one —
      // the rightmost non-background column (x2) and bottom row (y2) were excluded.
      // Fix: subimage(x1, y1, x2-x1+1, y2-y1+1).
      test("autocrop includes the rightmost and bottom content pixels") {
         // 5x5 image: 1-pixel white border, 3x3 red center at cols 1..3 / rows 1..3
         val pixels = Array(25) { i ->
            val x = i % 5; val y = i / 5
            val isCenter = x in 1..3 && y in 1..3
            val c = if (isCenter) Color.RED else Color.WHITE
            Pixel(x, y, c.red, c.green, c.blue, 255)
         }
         val image = ImmutableImage.create(5, 5, pixels)
         val cropped = image.autocrop(Color.WHITE)
         cropped.width shouldBe 3
         cropped.height shouldBe 3
         cropped.pixels().forEach { p ->
            p.red() shouldBe 255
            p.green() shouldBe 0
            p.blue() shouldBe 0
         }
      }
   }
}
