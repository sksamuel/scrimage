@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.GifWriter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class GifWriterTest : FunSpec({

   val original: ImmutableImage =
      ImmutableImage.loader().fromStream(javaClass.getResourceAsStream("/com/sksamuel/scrimage/bird.jpg"))
         .scaleTo(300, 200)

   test("GIF output happy path") {
      val actual = ImmutableImage.loader().fromBytes(original.bytes(GifWriter.Default))
      val expected = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/io/bird_compressed.gif")
      expected.pixels().size shouldBe actual.pixels().size
      expected shouldBe actual
   }

   test("GIF progressive output happy path") {
      val actual = ImmutableImage.loader().fromBytes(original.bytes(GifWriter.Progressive))
      val expected = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/io/bird_progressive.gif")
      expected.pixels().size shouldBe actual.pixels().size
      expected shouldBe actual
   }

})
