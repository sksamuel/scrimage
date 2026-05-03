package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.nio.AnimatedGifReader
import com.sksamuel.scrimage.nio.ImageSource
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import java.io.File

class AnimatedGifBoundsTest : FunSpec({

   // Regression: AnimatedGif accessors used to silently return wrong data
   // for out-of-bounds frame indices.
   //
   //  - getFrame(n) wrapped a null BufferedImage from reader.getFrame and
   //    NPE'd on the next pixel access (AwtImage's `assert awt != null`
   //    is a no-op in production JVMs).
   //  - getDelay returned Duration.ofMillis(-1), a negative duration with
   //    no indication that the index was bad.
   //  - getDisposeMethod silently returned NONE.
   //
   // AnimatedWebp's List.get already throws IndexOutOfBoundsException; this
   // change brings AnimatedGif in line.

   val gif = AnimatedGifReader.read(
      ImageSource.of(File("src/test/resources/com/sksamuel/scrimage/nio/animated_birds.gif"))
   )

   test("getFrame throws IndexOutOfBoundsException for negative index") {
      val ex = shouldThrow<IndexOutOfBoundsException> { gif.getFrame(-1) }
      ex.message!!.shouldContain("-1")
   }

   test("getFrame throws IndexOutOfBoundsException for index >= frameCount") {
      val ex = shouldThrow<IndexOutOfBoundsException> { gif.getFrame(gif.frameCount) }
      ex.message!!.shouldContain(gif.frameCount.toString())
   }

   test("getDelay throws IndexOutOfBoundsException for invalid index") {
      shouldThrow<IndexOutOfBoundsException> { gif.getDelay(gif.frameCount + 5) }
   }

   test("getDisposeMethod throws IndexOutOfBoundsException for invalid index") {
      shouldThrow<IndexOutOfBoundsException> { gif.getDisposeMethod(-2) }
   }
})
