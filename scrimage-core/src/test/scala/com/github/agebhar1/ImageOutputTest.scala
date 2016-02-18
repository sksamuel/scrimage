package com.github.agebhar1

import java.io.File
import java.nio.file.{Files, Path}

import com.sksamuel.scrimage.nio.PngWriter
import com.sksamuel.scrimage.{Color, Image}
import org.scalatest.{Matchers, WordSpec}

class ImageOutputTest extends WordSpec with Matchers {

  val image = Image.apply(180, 90).fill(Color.White)

  val path = File.createTempFile("ImageWriter", ".png").toPath
  image
    .output(path)(PngWriter.MaxCompression)

  "Image.output(path: Path)" should {
    "use default PngWriter.MaxCompression" in {

      val pathImplicit: Path = File.createTempFile("ImageWriter", ".png").toPath
      image.output(pathImplicit)

      assert(Files.readAllBytes(path) === Files.readAllBytes(pathImplicit))

    }
  }

  "Image.output(file: File)" should {
    "use default PngWriter.MaxCompression" in {

      val fileImplicit: File = File.createTempFile("ImageWriter", ".png")
      image.output(fileImplicit)

      assert(Files.readAllBytes(path) === Files.readAllBytes(fileImplicit.toPath))

    }
  }

  "Image.output(path: String)" should {
    "use default PngWriter.MaxCompression" in {

      val pathImplicit: String = File.createTempFile("ImageWriter", ".png").toString
      image.output(pathImplicit)

      assert(Files.readAllBytes(path) === Files.readAllBytes(new File(pathImplicit).toPath))

    }
  }

}
