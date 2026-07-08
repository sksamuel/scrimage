package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.MutableImage
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

   test("fillInPlace on a subimage view does not modify parent pixels outside the view") {
      // A getSubimage view shares the parent's DataBufferInt with a translated
      // origin and the parent's scanline stride, so the Arrays.fill fast path
      // used to overwrite the parent's entire buffer.
      val blue = 0xFF0000FF.toInt()
      val red = 0xFFFF0000.toInt()
      val parent = BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB)
      for (y in 0 until 8) {
         for (x in 0 until 8) {
            parent.setRGB(x, y, blue)
         }
      }

      val view = MutableImage(parent.getSubimage(2, 2, 4, 4))
      view.fillInPlace(RGBColor(255, 0, 0, 255).awt())

      for (y in 0 until 8) {
         for (x in 0 until 8) {
            val inside = x in 2..5 && y in 2..5
            parent.getRGB(x, y) shouldBe if (inside) red else blue
         }
      }
   }

   test("fillInPlace on a subimage view fills the view completely") {
      val parent = BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB)
      val view = MutableImage(parent.getSubimage(2, 2, 4, 4))
      view.fillInPlace(RGBColor(100, 200, 50, 255).awt())
      for (y in 0 until 4) {
         for (x in 0 until 4) {
            view.awt().getRGB(x, y) shouldBe 0xFF64C832.toInt()
         }
      }
   }
})
