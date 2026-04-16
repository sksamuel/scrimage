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
