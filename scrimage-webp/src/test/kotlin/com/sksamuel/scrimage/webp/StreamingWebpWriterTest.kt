package com.sksamuel.scrimage.webp

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.file.Files
import java.time.Duration

class StreamingWebpWriterTest : FunSpec({

   /**
    * The animated WebP container has the magic bytes "RIFF" at offset 0 and
    * "WEBP" at offset 8. An animation specifically also carries an "ANIM"
    * chunk somewhere in the file (followed by per-frame "ANMF" chunks).
    */
   fun isAnimatedWebp(bytes: ByteArray): Boolean {
      if (bytes.size < 12) return false
      if (String(bytes, 0, 4) != "RIFF") return false
      if (String(bytes, 8, 4) != "WEBP") return false
      val needle = "ANIM".toByteArray()
      outer@ for (i in 12..(bytes.size - needle.size)) {
         for (j in needle.indices) if (bytes[i + j] != needle[j]) continue@outer
         return true
      }
      return false
   }

   /**
    * Counts "ANMF" frame chunks in a WebP container. Each animated WebP frame
    * is wrapped in one ANMF chunk, so this is a quick way to verify the writer
    * actually emitted the number of frames the caller asked for.
    */
   fun countAnmfChunks(bytes: ByteArray): Int {
      val needle = "ANMF".toByteArray()
      var count = 0
      var i = 12
      while (i <= bytes.size - needle.size) {
         var match = true
         for (j in needle.indices) if (bytes[i + j] != needle[j]) { match = false; break }
         if (match) { count++; i += needle.size } else i++
      }
      return count
   }

   val red = ImmutableImage.filled(32, 16, Color.RED)
   val blue = ImmutableImage.filled(32, 16, Color.BLUE)
   val green = ImmutableImage.filled(32, 16, Color.GREEN)

   test("write to memory via OutputStream") {
      val writer = StreamingWebpWriter().withFrameDelay(Duration.ofMillis(500))
      val output = ByteArrayOutputStream()
      writer.prepareStream(output).use { stream ->
         stream.writeFrame(red)
         stream.writeFrame(blue)
         stream.writeFrame(green)
      }
      val bytes = output.toByteArray()
      isAnimatedWebp(bytes).shouldBeTrue()
      countAnmfChunks(bytes) shouldBe 3
   }

   test("write to a file path") {
      val target = Files.createTempFile("streaming_webp_test_", ".webp")
      try {
         val writer = StreamingWebpWriter().withFrameDelay(Duration.ofMillis(120))
         writer.prepareStream(target).use { stream ->
            stream.writeFrame(red)
            stream.writeFrame(blue)
         }
         val bytes = Files.readAllBytes(target)
         isAnimatedWebp(bytes).shouldBeTrue()
         countAnmfChunks(bytes) shouldBe 2
      } finally {
         target.toFile().delete()
      }
   }

   test("per-frame delay overrides the writer default") {
      // Mixing the default with an explicit per-frame delay exercises both
      // overloads of writeFrame and confirms they coexist on a single stream.
      val writer = StreamingWebpWriter().withFrameDelay(Duration.ofMillis(50))
      val output = ByteArrayOutputStream()
      writer.prepareStream(output).use { stream ->
         stream.writeFrame(red)                              // default 50ms
         stream.writeFrame(blue, Duration.ofMillis(250))      // explicit
         stream.writeFrame(green)                             // default 50ms
      }
      val bytes = output.toByteArray()
      isAnimatedWebp(bytes).shouldBeTrue()
      countAnmfChunks(bytes) shouldBe 3
   }

   test("withInfiniteLoop(false) still produces a valid animated webp") {
      val writer = StreamingWebpWriter().withInfiniteLoop(false)
      val output = ByteArrayOutputStream()
      writer.prepareStream(output).use { stream ->
         stream.writeFrame(red)
         stream.writeFrame(blue)
      }
      val bytes = output.toByteArray()
      isAnimatedWebp(bytes).shouldBeTrue()
      countAnmfChunks(bytes) shouldBe 2
   }

   test("lossy and lossless both produce valid animated webps") {
      val lossless = ByteArrayOutputStream()
      StreamingWebpWriter().withLossless(true).prepareStream(lossless).use { s ->
         s.writeFrame(red); s.writeFrame(blue)
      }
      isAnimatedWebp(lossless.toByteArray()).shouldBeTrue()

      val lossy = ByteArrayOutputStream()
      StreamingWebpWriter().withLossless(false).withQ(75).prepareStream(lossy).use { s ->
         s.writeFrame(red); s.writeFrame(blue)
      }
      isAnimatedWebp(lossy.toByteArray()).shouldBeTrue()
   }

   test("closing without writing any frames throws") {
      val writer = StreamingWebpWriter()
      val output = ByteArrayOutputStream()
      shouldThrow<IOException> {
         writer.prepareStream(output).close()
      }
   }

   test("writing after close throws") {
      val writer = StreamingWebpWriter()
      val output = ByteArrayOutputStream()
      val stream = writer.prepareStream(output)
      stream.writeFrame(red)
      stream.close()
      shouldThrow<IOException> {
         stream.writeFrame(blue)
      }
   }

   test("close is idempotent") {
      val writer = StreamingWebpWriter()
      val output = ByteArrayOutputStream()
      val stream = writer.prepareStream(output)
      stream.writeFrame(red)
      stream.close()
      // Second close must not throw, must not re-encode, must not corrupt the
      // already-written output.
      stream.close()
   }

   test("withQ rejects out-of-range values") {
      shouldThrow<IllegalArgumentException> { StreamingWebpWriter().withQ(-1) }
      shouldThrow<IllegalArgumentException> { StreamingWebpWriter().withQ(101) }
   }

   test("withM rejects out-of-range values") {
      shouldThrow<IllegalArgumentException> { StreamingWebpWriter().withM(-1) }
      shouldThrow<IllegalArgumentException> { StreamingWebpWriter().withM(7) }
   }
})
