package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.ScaleMethod
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

/**
 * Regression test for resampling TYPE_USHORT_GRAY images (16-bit greyscale PNGs).
 *
 * The resampler read USHORT_GRAY pixels via getRGB, which returns the sRGB-encoded value of
 * the linear CS_GRAY sample, but wrote the result back as a raw 16-bit sample. The gamma
 * curve was therefore applied twice, brightening every Bicubic/Bilinear/BSpline/Lanczos3
 * scale (e.g. a uniform raw 0x8000 image read as 0xbc came back as 0xdf).
 */
class ResampleUshortGrayTest : FunSpec({

   fun uniformGray(raw: Int): BufferedImage {
      val gray = BufferedImage(8, 8, BufferedImage.TYPE_USHORT_GRAY)
      for (y in 0 until 8) for (x in 0 until 8) gray.raster.setSample(x, y, 0, raw)
      return gray
   }

   fun maxChannelDiff(a: Int, b: Int): Int =
      listOf(24, 16, 8, 0).maxOf { s -> Math.abs(((a ushr s) and 255) - ((b ushr s) and 255)) }

   for (method in listOf(ScaleMethod.Bicubic, ScaleMethod.Bilinear, ScaleMethod.BSpline, ScaleMethod.Lanczos3)) {
      test("$method scale of a uniform TYPE_USHORT_GRAY image keeps the grey level") {
         for (raw in listOf(0, 0x1234, 0x8000, 0xC000, 0xFFFF)) {
            val img = ImmutableImage.wrapAwt(uniformGray(raw))
            val src = img.argbints()[0]
            val scaled = img.scaleTo(16, 16, method)
            scaled.argbints().forEach { maxChannelDiff(it, src) shouldBeLessThanOrEqual 1 }
         }
      }
   }

   test("a loaded 16-bit grey PNG keeps its grey level when scaled with the default method") {
      val bytes = ByteArrayOutputStream().also { ImageIO.write(uniformGray(0x8000), "png", it) }.toByteArray()
      val loaded = ImmutableImage.loader().fromBytes(bytes)
      loaded.type shouldBe BufferedImage.TYPE_USHORT_GRAY
      val src = loaded.argbints()[0]
      loaded.scaleTo(16, 16).argbints().forEach { maxChannelDiff(it, src) shouldBeLessThanOrEqual 1 }
   }
})
