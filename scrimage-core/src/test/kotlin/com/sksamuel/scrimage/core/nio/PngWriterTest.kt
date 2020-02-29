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
   }

})
