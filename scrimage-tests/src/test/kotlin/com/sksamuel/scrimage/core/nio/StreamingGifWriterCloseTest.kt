package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.AnimatedGifReader
import com.sksamuel.scrimage.nio.ImageSource
import com.sksamuel.scrimage.nio.StreamingGifWriter
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * Regression for [StreamingGifWriter.GifStream.close] not being idempotent. A second close()
 * called `writer.endWriteSequence()` on the already disposed ImageWriter (IllegalStateException),
 * then the finally chain closed the already closed MemoryCacheImageOutputStream, which threw a
 * NullPointerException that is what the caller saw. AutoCloseable asks implementations to make
 * close idempotent, and try-with-resources plus an explicit close() (or two `use` blocks) hit this.
 */
class StreamingGifWriterCloseTest : FunSpec({

   fun frame() = ImmutableImage.filled(4, 4, Color.RED)

   test("close is idempotent") {
      val out = ByteArrayOutputStream()
      val stream = StreamingGifWriter().prepareStream(out, BufferedImage.TYPE_INT_ARGB)
      stream.writeFrame(frame())
      stream.close()
      stream.close()
      // the output written by the first close is intact
      AnimatedGifReader.read(ImageSource.of(out.toByteArray())).frameCount shouldBe 1
   }

   test("writeFrame after close throws IOException") {
      val out = ByteArrayOutputStream()
      val stream = StreamingGifWriter().prepareStream(out, BufferedImage.TYPE_INT_ARGB)
      stream.writeFrame(frame())
      stream.close()
      val e = shouldThrow<IOException> { stream.writeFrame(frame()) }
      e.message shouldContain "closed"
   }
})
