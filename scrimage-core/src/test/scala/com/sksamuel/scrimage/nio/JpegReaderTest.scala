package com.sksamuel.scrimage.nio

import java.io.File
import java.nio.file.Files

import org.scalatest.{ Matchers, WordSpec }

class JpegReaderTest extends WordSpec with Matchers {

  "ImageIO" should {
    "be able to read all jpegs" in {
      val files = new File(getClass.getResource("/jpeg").getFile).listFiles()
      val images = files map { file => JavaImageIOReader.read(Files.readAllBytes(file.toPath)) }
      files.size shouldBe images.length
    }
  }
}
