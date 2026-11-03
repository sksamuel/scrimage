@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.GifWriter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

/**
 * Regression: the JDK GIF writer's write() path reads single band byte rasters from the parent's
 * origin, ignoring the translation of a sub-raster view. GifWriter passed the image straight
 * through, so wrapping a BufferedImage.getSubimage view of a TYPE_BYTE_INDEXED or TYPE_BYTE_GRAY
 * image and writing it as a GIF produced the wrong pixels.
 */
class GifWriterSubRasterTest : FunSpec({

   fun parent(type: Int): BufferedImage {
      val bi = BufferedImage(16, 8, type)
      for (y in 0 until 8) for (x in 0 until 16) {
         bi.setRGB(x, y, (0xff shl 24) or (((x * 16 + 7) and 255) shl 16) or (((y * 32 + 11) and 255) shl 8) or (((x + y) * 13 + 3) and 255))
      }
      return bi
   }

   fun decode(image: BufferedImage): IntArray {
      val decoded = ImageIO.read(ByteArrayInputStream(ImmutableImage.wrapAwt(image).bytes(GifWriter.Default)))
      return decoded.getRGB(0, 0, decoded.width, decoded.height, null, 0, decoded.width)
   }

   // a standalone image with the same colour model and samples as the view
   fun detachedCopy(view: BufferedImage): BufferedImage {
      val raster = view.colorModel.createCompatibleWritableRaster(view.width, view.height)
      raster.setRect(view.raster)
      return BufferedImage(view.colorModel, raster, view.isAlphaPremultiplied, null)
   }

   for ((name, type) in listOf("TYPE_BYTE_INDEXED" to BufferedImage.TYPE_BYTE_INDEXED, "TYPE_BYTE_GRAY" to BufferedImage.TYPE_BYTE_GRAY)) {
      test("a $name sub-raster view is written with its own pixels") {
         val view = parent(type).getSubimage(1, 1, 14, 6)
         decode(view).toList() shouldBe decode(detachedCopy(view)).toList()
      }
   }

   test("a TYPE_BYTE_INDEXED sub-raster view decodes to the view's colours") {
      val view = parent(BufferedImage.TYPE_BYTE_INDEXED).getSubimage(1, 1, 14, 6)
      decode(view).toList() shouldBe view.getRGB(0, 0, 14, 6, null, 0, 14).toList()
   }
})
