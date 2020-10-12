@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.TiffWriter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TiffWriterTest : FunSpec({

   val original: ImmutableImage =
      ImmutableImage.loader().fromStream(javaClass.getResourceAsStream("/com/sksamuel/scrimage/bird.jpg"))
         .scaleTo(300, 200)

   test("TIFF output happy path") {
      val actual = ImmutableImage.loader().fromBytes(original.bytes(TiffWriter.Default))
      val expected =
         ImmutableImage.loader().fromStream(javaClass.getResourceAsStream("/com/sksamuel/scrimage/nio/bird.tiff"))
      expected.pixels().size shouldBe actual.pixels().size
      expected shouldBe actual
   }

   test("TIFF output with compression") {
      val actual = ImmutableImage.loader().fromBytes(original.bytes(TiffWriter.Default.withCompressionType("ZLib")))
      val expected =
         ImmutableImage.loader().fromStream(javaClass.getResourceAsStream("/com/sksamuel/scrimage/nio/bird_zlib.tiff"))
      expected.pixels().size shouldBe actual.pixels().size
      expected shouldBe actual
   }
})
