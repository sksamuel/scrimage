package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.nio.JpegWriter
import com.sksamuel.scrimage.ImageWriter
import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

import org.apache.commons.io.IOUtils

class OrientationTest : WordSpec({

   fun readImageAndWriteToBytes(filePath: String, writer: ImageWriter): ByteArray =
      ImmutableImage.fromResource(filePath).bytes(writer)

   fun readBytes(filePath: String): ByteArray =
      IOUtils.toByteArray(javaClass.getResourceAsStream(filePath))

   "iphone image" should {
      "be re-orientated"  {
         // Images from: https://github.com/recurser/exif-orientation-examples

         val writer = JpegWriter(100, true)

         readImageAndWriteToBytes("/com/sksamuel/scrimage/iphone/portrait_1.jpg", writer) shouldBe
            readBytes("/com/sksamuel/scrimage/iphone/portrait_1_expected.jpg")

         readImageAndWriteToBytes("/com/sksamuel/scrimage/iphone/portrait_2.jpg", writer) shouldBe
            readBytes("/com/sksamuel/scrimage/iphone/portrait_2_expected.jpg")

         readImageAndWriteToBytes("/com/sksamuel/scrimage/iphone/portrait_3.jpg", writer) shouldBe
            readBytes("/com/sksamuel/scrimage/iphone/portrait_3_expected.jpg")

         readImageAndWriteToBytes("/com/sksamuel/scrimage/iphone/portrait_4.jpg", writer) shouldBe
            readBytes("/com/sksamuel/scrimage/iphone/portrait_4_expected.jpg")

         readImageAndWriteToBytes("/com/sksamuel/scrimage/iphone/portrait_5.jpg", writer) shouldBe
            readBytes("/com/sksamuel/scrimage/iphone/portrait_5_expected.jpg")

         readImageAndWriteToBytes("/com/sksamuel/scrimage/iphone/portrait_6.jpg", writer) shouldBe
            readBytes("/com/sksamuel/scrimage/iphone/portrait_6_expected.jpg")

         readImageAndWriteToBytes("/com/sksamuel/scrimage/iphone/portrait_7.jpg", writer) shouldBe
            readBytes("/com/sksamuel/scrimage/iphone/portrait_7_expected.jpg")

         readImageAndWriteToBytes("/com/sksamuel/scrimage/iphone/portrait_8.jpg", writer) shouldBe
            readBytes("/com/sksamuel/scrimage/iphone/portrait_8_expected.jpg")

         ImmutableImage.fromResource("/com/sksamuel/scrimage/iphone/up.JPG").width shouldBe 1280
      }

      "rotate image when the image and its thumbnail have the same rotation (issue #93)"  {
         readImageAndWriteToBytes("/issue93.jpg", JpegWriter(100, false)) shouldBe
            readBytes("/issue93_expected.jpg")
      }

      "rotate image when the image and its thumbnail different rotations (issue #114)"  {
         readImageAndWriteToBytes("/issue114.jpg", JpegWriter(100, false)) shouldBe
            readBytes("/issue114_expected.jpg")
      }
   }


})
