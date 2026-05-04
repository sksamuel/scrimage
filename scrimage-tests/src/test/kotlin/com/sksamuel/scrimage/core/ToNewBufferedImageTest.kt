package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.AwtImage
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.awt.image.BufferedImage

/**
 * Regression test for AwtImage.toNewBufferedImage(int) losing channel
 * precision when the requested type matches the source type. The previous
 * implementation always populated the target via Graphics2D.drawImage,
 * which composites via SrcOver and premultiplies alpha — losing ±1 per
 * channel when alpha doesn't divide 255 cleanly. Same root cause as the
 * clone(int) fix in #414.
 */
class ToNewBufferedImageTest : FunSpec({

   test("toNewBufferedImage(sameType) preserves channels exactly when alpha != 255") {
      val pixels = arrayOf(Pixel(0, 0, 100, 150, 200, 128))
      val image = ImmutableImage.create(1, 1, pixels)
      val target = image.toNewBufferedImage(BufferedImage.TYPE_INT_ARGB)
      val argb = target.getRGB(0, 0)
      ((argb ushr 24) and 0xFF) shouldBe 128
      ((argb ushr 16) and 0xFF) shouldBe 100
      ((argb ushr 8) and 0xFF) shouldBe 150
      (argb and 0xFF) shouldBe 200
   }

   test("toNewBufferedImage(sameType) returns a non-shared buffer") {
      val pixels = arrayOf(Pixel(0, 0, 100, 150, 200, 255))
      val image = ImmutableImage.create(1, 1, pixels)
      val target = image.toNewBufferedImage(BufferedImage.TYPE_INT_ARGB)
      target shouldNotBe image.awt()
      // Mutating the new buffer must not affect the original
      target.setRGB(0, 0, 0)
      image.pixel(0, 0).red() shouldBe 100
   }

   test("toNewBufferedImage with a different type still converts via Graphics2D") {
      val pixels = arrayOf(Pixel(0, 0, 100, 150, 200, 128))
      val image = ImmutableImage.create(1, 1, pixels)
      val target = image.toNewBufferedImage(BufferedImage.TYPE_INT_RGB)
      target.type shouldBe BufferedImage.TYPE_INT_RGB
      // TYPE_INT_RGB has no alpha channel — it always reports 255 on getRGB
      val argb = target.getRGB(0, 0)
      ((argb ushr 24) and 0xFF) shouldBe 255
   }

   test("toNewBufferedImage(sameType) preserves channels for TYPE_4BYTE_ABGR") {
      val src = BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR)
      src.setRGB(0, 0, 0x80FF6432.toInt()) // alpha=128, r=255, g=100, b=50
      val image = AwtImage(src)
      val target = image.toNewBufferedImage(BufferedImage.TYPE_4BYTE_ABGR)
      target.getRGB(0, 0) shouldBe 0x80FF6432.toInt()
   }
})
