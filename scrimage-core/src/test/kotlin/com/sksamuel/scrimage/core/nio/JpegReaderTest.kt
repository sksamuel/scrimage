package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.nio.ImageIOReader
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import java.io.File
import java.nio.file.Files

class JpegReaderTest : WordSpec({

   "ImageIO" should {
      "be able to read all jpegs"  {
         val files = File(javaClass.getResource("/jpeg").file).listFiles()
         val images = files.map { file -> ImageIOReader().read(Files.newInputStream(file.toPath()), null) }
         files.size shouldBe images.size
      }
   }

})
