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
         val images = files.map { file -> PngReader().read(Files.newInputStream(file.toPath()), null) }
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
   }

})
