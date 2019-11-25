package com.sksamuel.scrimage

import com.sksamuel.scrimage.nio.JpegWriter
import org.apache.commons.io.IOUtils
import org.scalatest.{Matchers, WordSpec}

class OrientationTest extends WordSpec with Matchers {

  "iphone image" should {
    "be re-orientated" ignore {
      // Images from: https://github.com/recurser/exif-orientation-examples

      val writer = new JpegWriter(100, true)

      readImageAndWriteToBytes("/com/sksamuel/scrimage/iphone/portrait_1.jpg", writer) shouldBe readBytes("/com/sksamuel/scrimage/iphone/portrait_1_expected.jpg")
      readImageAndWriteToBytes("/com/sksamuel/scrimage/iphone/portrait_2.jpg", writer) shouldBe readBytes("/com/sksamuel/scrimage/iphone/portrait_2_expected.jpg")
      readImageAndWriteToBytes("/com/sksamuel/scrimage/iphone/portrait_3.jpg", writer) shouldBe readBytes("/com/sksamuel/scrimage/iphone/portrait_3_expected.jpg")
      readImageAndWriteToBytes("/com/sksamuel/scrimage/iphone/portrait_4.jpg", writer) shouldBe readBytes("/com/sksamuel/scrimage/iphone/portrait_4_expected.jpg")
      readImageAndWriteToBytes("/com/sksamuel/scrimage/iphone/portrait_5.jpg", writer) shouldBe readBytes("/com/sksamuel/scrimage/iphone/portrait_5_expected.jpg")
      readImageAndWriteToBytes("/com/sksamuel/scrimage/iphone/portrait_6.jpg", writer) shouldBe readBytes("/com/sksamuel/scrimage/iphone/portrait_6_expected.jpg")
      readImageAndWriteToBytes("/com/sksamuel/scrimage/iphone/portrait_7.jpg", writer) shouldBe readBytes("/com/sksamuel/scrimage/iphone/portrait_7_expected.jpg")
      readImageAndWriteToBytes("/com/sksamuel/scrimage/iphone/portrait_8.jpg", writer) shouldBe readBytes("/com/sksamuel/scrimage/iphone/portrait_8_expected.jpg")

      Image.fromResource("/com/sksamuel/scrimage/iphone/up.JPG").width shouldBe 1280
    }

    "rotate image when the image and its thumbnail have the same rotation (issue #93)" in {
      readImageAndWriteToBytes("/issue93.jpg", new JpegWriter(100, false)) shouldBe readBytes("/issue93_expected.jpg")
    }

    "rotate image when the image and its thumbnail different rotations (issue #114)" in {
      readImageAndWriteToBytes("/issue114.jpg", new JpegWriter(100, false)) shouldBe readBytes("/issue114_expected.jpg")
    }
  }

  private def readImageAndWriteToBytes(filePath: String, writer: ImageWriter): Array[Byte] =
    Image.fromResource(filePath).bytes(writer)

  private def readBytes(filePath: String): Array[Byte] =
    IOUtils.toByteArray(getClass.getResourceAsStream(filePath))
}
