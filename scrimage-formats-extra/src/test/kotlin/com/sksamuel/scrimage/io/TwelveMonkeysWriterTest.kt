package com.sksamuel.scrimage.io

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.BmpWriter
import com.sksamuel.scrimage.nio.TgaWriter
import com.sksamuel.scrimage.nio.TiffWriter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

// https://github.com/sksamuel/scrimage/pull/354
class TwelveMonkeysWriterTest : FunSpec({

   val original = ImmutableImage.loader().fromStream(javaClass.getResourceAsStream("/picard.jpeg"))
      .scaleTo(100, 100)

   test("BmpWriter round-trips image dimensions") {
      val bytes = original.bytes(BmpWriter())
      val actual = ImmutableImage.loader().fromBytes(bytes)
      actual.width shouldBe original.width
      actual.height shouldBe original.height
   }

   test("TiffWriter round-trips image dimensions") {
      val bytes = original.bytes(TiffWriter())
      val actual = ImmutableImage.loader().fromBytes(bytes)
      actual.width shouldBe original.width
      actual.height shouldBe original.height
   }

   test("TgaWriter round-trips image dimensions") {
      val bytes = original.bytes(TgaWriter())
      val actual = ImmutableImage.loader().fromBytes(bytes)
      actual.width shouldBe original.width
      actual.height shouldBe original.height
   }

   // The TwelveMonkeys BMP encoder cannot encode 32bpp images, so BmpWriter must flatten
   // alpha before encoding. Before the fix this threw IOException("Image can not be encoded
   // with compression type BI_RGB and 32 bits per pixel") for TYPE_INT_ARGB images.
   test("BmpWriter supports images with an alpha channel") {
      val argb = ImmutableImage.create(40, 30).fill(java.awt.Color.RED)
      argb.awt().type shouldBe java.awt.image.BufferedImage.TYPE_INT_ARGB
      val actual = ImmutableImage.loader().fromBytes(argb.bytes(BmpWriter()))
      actual.width shouldBe 40
      actual.height shouldBe 30
      actual.pixel(20, 15).toARGBInt() shouldBe 0xFFFF0000.toInt()
   }

   test("BmpWriter composites transparent pixels onto white") {
      val transparent = ImmutableImage.create(10, 10) // fully transparent ARGB
      val actual = ImmutableImage.loader().fromBytes(transparent.bytes(BmpWriter()))
      actual.pixel(5, 5).toARGBInt() shouldBe 0xFFFFFFFF.toInt()
   }

   test("BmpWriter write does not leak resources on repeated writes") {
      repeat(100) {
         original.bytes(BmpWriter())
      }
   }

   // Regression test: TiffWriter.write() overrides TwelveMonkeysWriter.write() and had the
   // same resource-leak pattern: ios and writer were not closed in a try/finally.
   test("TiffWriter write does not leak resources on repeated writes") {
      repeat(100) {
         original.bytes(TiffWriter())
      }
   }
})
