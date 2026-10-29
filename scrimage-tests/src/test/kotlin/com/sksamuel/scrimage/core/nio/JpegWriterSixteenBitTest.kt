@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.JpegWriter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import java.awt.Transparency
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ComponentColorModel
import java.awt.image.DataBuffer
import java.awt.image.Raster
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

/**
 * Regression: the JDK JPEG writer rejects samples wider than 8 bits ("Illegal band size"), and
 * JpegWriter only converted images with alpha. The default loader returns TYPE_USHORT_GRAY for a
 * 16-bit greyscale PNG and TYPE_CUSTOM for a 16-bit RGB PNG, so neither could be saved as a JPEG.
 */
class JpegWriterSixteenBitTest : FunSpec({

   fun png(image: BufferedImage): ByteArray {
      val baos = ByteArrayOutputStream()
      ImageIO.write(image, "png", baos)
      return baos.toByteArray()
   }

   test("a 16-bit greyscale png loaded by the default loader can be written as a jpeg") {
      val bi = BufferedImage(8, 8, BufferedImage.TYPE_USHORT_GRAY)
      for (y in 0 until 8) for (x in 0 until 8) bi.raster.setSample(x, y, 0, 0x8080)
      val loaded = ImmutableImage.loader().fromBytes(png(bi))
      loaded.type shouldBe BufferedImage.TYPE_USHORT_GRAY

      val decoded = ImageIO.read(ByteArrayInputStream(loaded.bytes(JpegWriter(100, false))))
      decoded.type shouldBe BufferedImage.TYPE_BYTE_GRAY
      // the stored sample value is kept (high byte of 0x8080), not brightened by a grey -> RGB conversion
      decoded.raster.getSample(3, 3, 0) shouldBeInRange 0x7f..0x81
   }

   test("a 16-bit rgb png loaded by the default loader can be written as a jpeg") {
      val cm = ComponentColorModel(
         ColorSpace.getInstance(ColorSpace.CS_sRGB), intArrayOf(16, 16, 16), false, false,
         Transparency.OPAQUE, DataBuffer.TYPE_USHORT
      )
      val raster = Raster.createInterleavedRaster(DataBuffer.TYPE_USHORT, 8, 8, 3, null)
      for (y in 0 until 8) for (x in 0 until 8) raster.setPixel(x, y, intArrayOf(0xC0C0, 0x4040, 0x8080))
      val loaded = ImmutableImage.loader().fromBytes(png(BufferedImage(cm, raster, false, null)))
      loaded.type shouldBe BufferedImage.TYPE_CUSTOM

      val decoded = ImageIO.read(ByteArrayInputStream(loaded.bytes(JpegWriter(100, false))))
      val rgb = decoded.getRGB(3, 3)
      (rgb shr 16 and 0xff) shouldBeInRange 0xbe..0xc2
      (rgb shr 8 and 0xff) shouldBeInRange 0x3e..0x42
      (rgb and 0xff) shouldBeInRange 0x7e..0x82
   }
})
