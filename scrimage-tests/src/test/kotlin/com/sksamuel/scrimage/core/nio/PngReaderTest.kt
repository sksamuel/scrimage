@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.Colors
import com.sksamuel.scrimage.nio.PngReader
import com.sksamuel.scrimage.nio.PngWriter
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import java.io.File
import java.nio.file.Files

class PngReaderTest : WordSpec({

   "PngReader" should {
      "be able to read pngs of all channels"  {
         val files = File(javaClass.getResource("/png").file).listFiles()!!
         val images = files.map { file -> PngReader().read(Files.readAllBytes(file.toPath()), null) }
         images.size shouldBe 13
      }
      "use backing awt image with transparency"  {
         // generate a png image
         val image = ImmutableImage.filled(10, 10, Colors.Transparent.awt())
         val png = image.bytes(PngWriter())

         // read the image normally
         val image2 = ImmutableImage.loader().fromBytes(png)
         image2.awt().colorModel.hasAlpha() shouldBe true
      }
      // Regression: isPng compares the 8 magic bytes in place; it must reject
      // short or non-png input by returning null rather than throwing.
      "return null for an empty byte array" {
         PngReader().read(ByteArray(0), null) shouldBe null
      }
      "return null for inputs shorter than the 8 byte png magic" {
         val magic = byteArrayOf(0x89.toByte(), 'P'.code.toByte(), 'N'.code.toByte(), 'G'.code.toByte(), 0x0D, 0x0A, 0x1A, 0x0A)
         for (len in 1..7) {
            PngReader().read(magic.copyOf(len), null) shouldBe null
         }
      }
      "return null for non-png bytes" {
         PngReader().read(ByteArray(16) { 0x42 }, null) shouldBe null
      }
      "return null for jpeg bytes" {
         val jpeg = javaClass.getResourceAsStream("/com/sksamuel/scrimage/bird.jpg")!!.readBytes()
         PngReader().read(jpeg, null) shouldBe null
      }
      "return null when the magic differs only in the final byte" {
         val nearlyPng = byteArrayOf(0x89.toByte(), 'P'.code.toByte(), 'N'.code.toByte(), 'G'.code.toByte(), 0x0D, 0x0A, 0x1A, 0x0B)
         PngReader().read(nearlyPng, null) shouldBe null
      }
   }

})
