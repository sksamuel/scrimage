package com.sksamuel.scrimage

import com.sksamuel.scrimage.nio.{JavaImageIOReader, JpegWriter, PngWriter}
import org.scalatest.{Matchers, WordSpec}

class Issue143Test extends WordSpec with Matchers {

  private val jpeg: Array[Byte] = {
    val image = Image.filled(10, 10)
    image.forWriter(JpegWriter()).bytes
  }

  "JavaImageIOReader" should {
    "extract image type from the processed image" in {
      val image = JavaImageIOReader.fromBytes(jpeg, Image.AUTODETECT_DATA_TYPE)
      image.get.awt.getColorModel.hasAlpha shouldBe false
    }
  }

  "JPEG Image" should {
    "not use backing awt image with transparency" in {
      // read the image normally
      val image = Image(bytes = jpeg, `type` = Image.AUTODETECT_DATA_TYPE)
      image.awt.getColorModel.hasAlpha shouldBe false
    }
  }

  "PNG Image" should {
    "use backing awt image with transparency" in {
      // generate a jpeg image
      val image = Image.filled(10, 10, Color.Transparent)
      val png = image.forWriter(PngWriter()).bytes

      // read the image normally
      val image2 = Image(bytes = png)
      image2.awt.getColorModel.hasAlpha shouldBe true
    }
  }
}
