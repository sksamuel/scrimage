@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.nio.PngReader
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import java.io.File
import java.nio.file.Files

class PngReaderTest : WordSpec({

   "PngReader" should {
      "be able to read pngs of all channels"  {
         val files = File(javaClass.getResource("/png").file).listFiles()
         val images = files.map { file -> PngReader().read(Files.newInputStream(file.toPath()), null) }
         images.size shouldBe 13
      }
   }

})
