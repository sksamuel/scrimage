@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.ImageIOReader
import com.sksamuel.scrimage.nio.JpegWriter
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Files

class JpegReaderTest : WordSpec({

   val jpeg = ImmutableImage.filled(10, 10, Color.white).bytes(JpegWriter())

   "ImageIO" should {
      "be able to read all jpegs"  {
         val files = File(javaClass.getResource("/jpeg").file).listFiles()!!
         val images = files.map { file -> ImageIOReader().read(Files.newInputStream(file.toPath()), null) }
         files.size shouldBe images.size
      }

      "extract image type from the processed image"  {
         val image = ImageIOReader().read(ByteArrayInputStream(jpeg))
         image.awt().colorModel.hasAlpha() shouldBe false
      }
   }

   "JPEG Image" should {
      "not use backing awt image with transparency"  {
         // read the image normally
         val image = ImmutableImage.loader().fromBytes(jpeg)
         image.awt().colorModel.hasAlpha() shouldBe false
      }
   }
})
