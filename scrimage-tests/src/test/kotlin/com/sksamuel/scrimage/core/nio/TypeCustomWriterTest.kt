@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.AnimatedGifReader
import com.sksamuel.scrimage.nio.GifSequenceWriter
import com.sksamuel.scrimage.nio.ImageSource
import com.sksamuel.scrimage.nio.PngWriter
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import java.awt.Transparency
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ComponentColorModel
import java.awt.image.DataBuffer
import java.awt.image.Raster
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

/**
 * Regression tests: PngWriter and GifSequenceWriter built their ImageTypeSpecifier with
 * ImageTypeSpecifier.createFromBufferedImageType(image.getType()), which throws
 * IllegalArgumentException("Cannot create from TYPE_CUSTOM!") when the image type is
 * TYPE_CUSTOM (0). The JDK PNG reader decodes 16-bit per channel PNGs to TYPE_CUSTOM,
 * so a 16-bit PNG loaded with ImmutableImage.loader() could not be written back out.
 */
class TypeCustomWriterTest : WordSpec({

   // a 16-bit per channel RGB image; BufferedImage reports these as TYPE_CUSTOM
   fun custom16bitRgbImage(w: Int, h: Int): BufferedImage {
      val cs = ColorSpace.getInstance(ColorSpace.CS_sRGB)
      val cm = ComponentColorModel(cs, intArrayOf(16, 16, 16), false, false, Transparency.OPAQUE, DataBuffer.TYPE_USHORT)
      val raster = Raster.createInterleavedRaster(DataBuffer.TYPE_USHORT, w, h, 3, null)
      for (y in 0 until h) {
         for (x in 0 until w) {
            raster.setPixel(x, y, intArrayOf(0x8080, 0x4040, 0xC0C0))
         }
      }
      return BufferedImage(cm, raster, false, null)
   }

   // a 16-bit RGB png as produced by any external tool; the JDK reader decodes it to TYPE_CUSTOM
   fun sixteenBitPng(w: Int, h: Int): ByteArray {
      val baos = ByteArrayOutputStream()
      ImageIO.write(custom16bitRgbImage(w, h), "png", baos)
      return baos.toByteArray()
   }

   "PngWriter" should {
      "write an image backed by a TYPE_CUSTOM BufferedImage" {
         val awt = custom16bitRgbImage(20, 10)
         awt.type shouldBe BufferedImage.TYPE_CUSTOM
         val image = ImmutableImage.wrapAwt(awt)
         val bytes = image.bytes(PngWriter())
         val decoded = ImmutableImage.loader().fromBytes(bytes)
         decoded.width shouldBe 20
         decoded.height shouldBe 10
         decoded.pixel(0, 0).argb shouldBe 0xFF8040C0.toInt()
      }
      "round-trip a 16-bit png loaded from bytes" {
         val png = sixteenBitPng(20, 10)
         val image = ImmutableImage.loader().fromBytes(png)
         image.awt().type shouldBe BufferedImage.TYPE_CUSTOM
         val bytes = image.bytes(PngWriter())
         val decoded = ImmutableImage.loader().fromBytes(bytes)
         decoded.width shouldBe 20
         decoded.height shouldBe 10
         decoded.pixel(0, 0).argb shouldBe image.pixel(0, 0).argb
      }
   }

   "GifSequenceWriter" should {
      "write frames backed by TYPE_CUSTOM BufferedImages" {
         val frame = ImmutableImage.loader().fromBytes(sixteenBitPng(20, 10))
         frame.awt().type shouldBe BufferedImage.TYPE_CUSTOM
         val bytes = GifSequenceWriter().withFrameDelay(100).bytes(arrayOf(frame, frame))
         val decoded = AnimatedGifReader.read(ImageSource.of(bytes))
         decoded.frameCount shouldBe 2
         decoded.getFrame(0).width shouldBe 20
         decoded.getFrame(0).height shouldBe 10
      }
   }

})
