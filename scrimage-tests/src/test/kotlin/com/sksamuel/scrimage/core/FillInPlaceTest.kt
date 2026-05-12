package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.RGBColor
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage

/**
 * Pin-down tests for fillInPlace across both code paths:
 *
 *  - DataBufferInt fast path (TYPE_INT_ARGB) uses Arrays.fill on the
 *    backing int[] directly
 *  - the generic fallback (TYPE_4BYTE_ABGR or other non-int buffers)
 *    builds an int[] once and bulk setRGBs it
 *
 * Both paths must produce the same pixel values when the source colour
 * round-trips cleanly through the image's colour model.
 */
class FillInPlaceTest : FunSpec({

   test("fillInPlace fills every pixel for TYPE_INT_ARGB (fast path)") {
      val image = ImmutableImage.create(3, 2, BufferedImage.TYPE_INT_ARGB)
      image.fillInPlace(RGBColor(100, 200, 50, 255).awt())
      image.pixels().forEach { p ->
         p.red() shouldBe 100
         p.green() shouldBe 200
         p.blue() shouldBe 50
         p.alpha() shouldBe 255
      }
   }

   test("fillInPlace fills every pixel for TYPE_4BYTE_ABGR (fallback path)") {
      val image = ImmutableImage.create(3, 2, BufferedImage.TYPE_4BYTE_ABGR)
      image.fillInPlace(RGBColor(100, 200, 50, 128).awt())
      image.pixels().forEach { p ->
         p.red() shouldBe 100
         p.green() shouldBe 200
         p.blue() shouldBe 50
         p.alpha() shouldBe 128
      }
   }

   test("fillInPlace overwrites existing pixel values rather than blending") {
      val image = ImmutableImage.create(2, 2, BufferedImage.TYPE_INT_ARGB)
      image.setColor(0, 0, RGBColor(255, 0, 0, 255))
      image.setColor(1, 0, RGBColor(0, 255, 0, 255))
      image.fillInPlace(RGBColor(0, 0, 255, 255).awt())
      image.pixels().forEach { p -> p.argb shouldBe 0xFF0000FF.toInt() }
   }
})
