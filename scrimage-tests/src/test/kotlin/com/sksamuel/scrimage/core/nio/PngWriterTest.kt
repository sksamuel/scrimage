@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PngReader
import com.sksamuel.scrimage.nio.PngWriter
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.io.IOUtils

class PngWriterTest : WordSpec({

   val writer: PngWriter = PngWriter.MaxCompression
   val original = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/bird.jpg").scaleTo(300, 200)

   "png write" should {
      "png output happy path"  {
         val bytes = original.bytes(writer)
         val expected = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/io/bird_300_200.png")
         expected.pixels().size shouldBe ImmutableImage.loader().fromBytes(bytes).pixels().size
         expected.pixels().toList() shouldBe ImmutableImage.loader().fromBytes(bytes).pixels().toList()
         expected shouldBe ImmutableImage.loader().fromBytes(bytes)
      }
      "png compression happy path"  {
         repeat(10) { k ->
            val w: PngWriter = PngWriter.NoCompression.withCompression(k)
            val bytes = original.bytes(w)
            val expected = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/io/bird_compressed_$k.png")
            expected.pixels().size shouldBe ImmutableImage.loader().fromBytes(bytes).pixels().size
            expected.pixels().toList() shouldBe ImmutableImage.loader().fromBytes(bytes).pixels().toList()
            expected shouldBe ImmutableImage.loader().fromBytes(bytes)
         }
      }
      "png reader reads an image correctly"  {
         val expected = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/io/bird_300_200.png")
         val bytes = IOUtils.toByteArray(javaClass.getResourceAsStream("/com/sksamuel/scrimage/io/bird_300_200.png"))
         val actual = PngReader().read(bytes, null)
         actual.width shouldBe expected.width
         actual.height shouldBe expected.height
         actual shouldBe expected
      }
      // Regression test: compressionLevel < 0 || compressionLevel >= 10 was written as && so the
      // validation was dead code — values outside 0-9 were silently accepted.
      "invalid compression level below 0 is rejected" {
         val ex = runCatching { original.bytes(PngWriter(-1)) }.exceptionOrNull()
         ex?.message shouldBe "Compression level must be between 0 (none) and 9 (max)"
      }
      "invalid compression level of 10 is rejected" {
         val ex = runCatching { original.bytes(PngWriter(10)) }.exceptionOrNull()
         ex?.message shouldBe "Compression level must be between 0 (none) and 9 (max)"
      }
      "valid boundary compression levels 0 and 9 are accepted" {
         original.bytes(PngWriter(0)).size shouldBe original.bytes(PngWriter.NoCompression).size
         original.bytes(PngWriter(9)).size shouldBe original.bytes(PngWriter.MaxCompression).size
      }
   }

})
