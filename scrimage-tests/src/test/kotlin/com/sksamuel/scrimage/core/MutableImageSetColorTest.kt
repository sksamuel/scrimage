package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.RGBColor
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage

/**
 * Pin-down tests for the offset → (x, y) mapping in MutableImage.setColor.
 * The optimisation drops a per-call Point allocation by inlining the
 * row-major arithmetic. The mapping must remain row-major: offset = y * w + x.
 */
class MutableImageSetColorTest : FunSpec({

   test("setColor(offset) writes to the row-major coordinate") {
      val w = 4; val h = 3
      val image = ImmutableImage.create(w, h, BufferedImage.TYPE_INT_ARGB)
      // Offset 0 → (0, 0); 1 → (1, 0); 4 → (0, 1); 7 → (3, 1); 11 → (3, 2)
      image.setColor(0, RGBColor(255, 0, 0, 255))
      image.setColor(1, RGBColor(0, 255, 0, 255))
      image.setColor(4, RGBColor(0, 0, 255, 255))
      image.setColor(7, RGBColor(255, 255, 0, 255))
      image.setColor(11, RGBColor(255, 0, 255, 255))

      image.pixel(0, 0).argb shouldBe 0xFFFF0000.toInt()
      image.pixel(1, 0).argb shouldBe 0xFF00FF00.toInt()
      image.pixel(0, 1).argb shouldBe 0xFF0000FF.toInt()
      image.pixel(3, 1).argb shouldBe 0xFFFFFF00.toInt()
      image.pixel(3, 2).argb shouldBe 0xFFFF00FF.toInt()
   }

   test("setColor(offset) and setColor(x, y) produce equivalent results") {
      val w = 5; val h = 4
      val a = ImmutableImage.create(w, h, BufferedImage.TYPE_INT_ARGB)
      val b = ImmutableImage.create(w, h, BufferedImage.TYPE_INT_ARGB)
      // Sweep every offset and write distinct colours
      for (i in 0 until w * h) {
         val color = RGBColor(i and 0xFF, (i * 7) and 0xFF, (i * 13) and 0xFF, 255)
         a.setColor(i, color)
         b.setColor(i % w, i / w, color)
      }
      a shouldBe b
   }
})
