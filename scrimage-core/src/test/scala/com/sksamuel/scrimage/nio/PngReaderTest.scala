package com.sksamuel.scrimage.nio

import java.io.File
import java.nio.file.Files

import org.apache.commons.io.{FileUtils, IOUtils}
import org.scalatest.{ Matchers, WordSpec }

class PngReaderTest extends WordSpec with Matchers {

  "PngReader" should {
    "be able to read all pngs" in {
      val files = new File(getClass.getResource("/png").getFile).listFiles()
      val images = files map { file => PngReader.read(Files.readAllBytes(file.toPath)) }
      files.size shouldBe images.length
    }
  }
}
