@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.nio.PngReader
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import java.awt.Color
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
 * Regression tests: PngReader (the fallback reader delegating to pngj) mishandled
 * bit depths other than 8 in the non-indexed branch:
 *
 * - bit depths 1/2/4 (grayscale): pngj returns unscaled samples (a 1-bit white pixel
 *   is 1, not 255) and ImageLineHelper.scaleUp was only called in the indexed branch,
 *   where it is an explicit no-op — so a 1-bit all-white png decoded to ff010101.
 * - bit depth 16: pngj returns 0-65535 samples and PixelTools.argb keeps only the low
 *   byte, so a uniform 0x8000 mid-grey decoded to black.
 *
 * The 16-bit sample values below (0x8000, 0x4000, 0x2000) have zero low bytes, so the
 * pre-fix reader decoded them all to 0; they are also values for which the JDK reader's
 * rounded conversion and the high-byte conversion agree exactly.
 *
 * PngReader is only a fallback (the default loader path uses ImageIOReader first),
 * so it is invoked directly here.
 */
class PngReaderBitDepthTest : WordSpec({

   fun png(image: BufferedImage): ByteArray {
      val baos = ByteArrayOutputStream()
      ImageIO.write(image, "png", baos)
      return baos.toByteArray()
   }

   fun component16bitImage(bands: Int, samples: IntArray): BufferedImage {
      val cs = ColorSpace.getInstance(ColorSpace.CS_sRGB)
      val hasAlpha = bands == 4
      val transparency = if (hasAlpha) Transparency.TRANSLUCENT else Transparency.OPAQUE
      val cm = ComponentColorModel(cs, IntArray(bands) { 16 }, hasAlpha, false, transparency, DataBuffer.TYPE_USHORT)
      val raster = Raster.createInterleavedRaster(DataBuffer.TYPE_USHORT, 4, 4, bands, null)
      for (y in 0 until 4) {
         for (x in 0 until 4) {
            raster.setPixel(x, y, samples)
         }
      }
      return BufferedImage(cm, raster, false, null)
   }

   "PngReader" should {

      "scale up 1-bit grayscale pngs" {
         // TYPE_BYTE_BINARY is written by the JDK as bit depth 1, color type 0 (grayscale)
         val awt = BufferedImage(4, 4, BufferedImage.TYPE_BYTE_BINARY)
         val g = awt.createGraphics()
         g.color = Color.WHITE
         g.fillRect(0, 0, 4, 2)
         g.dispose()
         val bytes = png(awt)

         val image = PngReader().read(bytes, null)
         val jdk = ImageIO.read(ByteArrayInputStream(bytes))
         image.pixel(0, 0).argb shouldBe 0xFFFFFFFF.toInt() // was ff010101
         image.pixel(0, 0).argb shouldBe jdk.getRGB(0, 0)
         image.pixel(0, 3).argb shouldBe 0xFF000000.toInt()
         image.pixel(0, 3).argb shouldBe jdk.getRGB(0, 3)
      }

      "decode 16-bit grayscale pngs using the high byte of each sample" {
         // TYPE_USHORT_GRAY is written by the JDK as bit depth 16, color type 0 (grayscale)
         val awt = BufferedImage(4, 4, BufferedImage.TYPE_USHORT_GRAY)
         for (y in 0 until 4) {
            for (x in 0 until 4) {
               awt.raster.setSample(x, y, 0, 0x8000)
            }
         }
         val bytes = png(awt)

         val image = PngReader().read(bytes, null)
         image.pixel(0, 0).argb shouldBe 0xFF808080.toInt() // was ff000000 (black)

         // the raw 16-bit sample as decoded by the JDK reader must agree
         val jdkGray = ImageIO.read(ByteArrayInputStream(bytes)).raster.getSample(0, 0, 0)
         image.pixel(0, 0).argb shouldBe (0xFF000000.toInt() or ((jdkGray shr 8) * 0x010101))
      }

      "decode 16-bit rgb pngs using the high byte of each sample" {
         // bit depth 16, color type 2 (rgb)
         val bytes = png(component16bitImage(3, intArrayOf(0x8000, 0x4000, 0x2000)))

         val image = PngReader().read(bytes, null)
         val jdk = ImageIO.read(ByteArrayInputStream(bytes))
         image.pixel(0, 0).argb shouldBe 0xFF804020.toInt() // was ff000000 (black)
         image.pixel(0, 0).argb shouldBe jdk.getRGB(0, 0)
      }

      "decode 16-bit rgba pngs including the alpha sample" {
         // bit depth 16, color type 6 (rgba)
         val bytes = png(component16bitImage(4, intArrayOf(0x8000, 0x4000, 0x2000, 0x8000)))

         val image = PngReader().read(bytes, null)
         val jdk = ImageIO.read(ByteArrayInputStream(bytes))
         image.pixel(0, 0).argb shouldBe 0x80804020.toInt() // was 00000000 (fully transparent black)
         image.pixel(0, 0).argb shouldBe jdk.getRGB(0, 0)
      }
   }

})
