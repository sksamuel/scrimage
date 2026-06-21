@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.nio.PngReader
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage
import java.awt.image.IndexColorModel
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

/**
 * Regression for PngReader dropping palette transparency (the tRNS chunk) when
 * decoding indexed (colour type 3) PNGs. It used to pass null for the tRNS chunk
 * and hard-code alpha 255, so an indexed PNG with one or more transparent palette
 * entries decoded fully opaque.
 */
class PngReaderIndexedTransparencyTest : WordSpec({

   // An indexed PNG with a 2-entry palette: index 0 = opaque red, index 1 = fully transparent.
   // ImageIO emits a PLTE + tRNS chunk for an IndexColorModel that carries alpha.
   fun indexedPngWithTransparency(): ByteArray {
      val reds = byteArrayOf(255.toByte(), 0)
      val greens = byteArrayOf(0, 0)
      val blues = byteArrayOf(0, 0)
      val alphas = byteArrayOf(255.toByte(), 0)
      val icm = IndexColorModel(8, 2, reds, greens, blues, alphas)
      val img = BufferedImage(2, 1, BufferedImage.TYPE_BYTE_INDEXED, icm)
      img.raster.setSample(0, 0, 0, 0) // opaque red
      img.raster.setSample(1, 0, 0, 1) // transparent
      val baos = ByteArrayOutputStream()
      ImageIO.write(img, "png", baos)
      return baos.toByteArray()
   }

   fun containsChunk(bytes: ByteArray, type: String): Boolean {
      val t = type.toByteArray(Charsets.US_ASCII)
      outer@ for (i in 0..bytes.size - t.size) {
         for (j in t.indices) if (bytes[i + j] != t[j]) continue@outer
         return true
      }
      return false
   }

   "PngReader" should {
      "honour tRNS palette transparency on indexed PNGs" {
         val bytes = indexedPngWithTransparency()

         // sanity check the fixture really is an indexed PNG carrying a tRNS chunk,
         // so this test exercises PngReader's indexed branch. IHDR colour type is the
         // byte at offset 25 (8 sig + 4 len + 4 "IHDR" + 4 width + 4 height + 1 bitdepth).
         bytes[25].toInt() shouldBe 3 // colour type 3 == indexed
         containsChunk(bytes, "tRNS") shouldBe true

         val image = PngReader().read(bytes, null)

         val opaque = image.pixel(0, 0)
         opaque.alpha() shouldBe 255
         opaque.red() shouldBe 255

         image.pixel(1, 0).alpha() shouldBe 0
      }
   }
})
