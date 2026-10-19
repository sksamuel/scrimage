package com.sksamuel.scrimage.webp

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.time.Duration

// img2webp requires a strictly positive frame duration. Previously a zero delay was
// accepted by writeFrame and only failed at close() with an opaque
// "img2webp exited with status 1: [Invalid negative duration (0)]" after every frame
// had been buffered, and a negative delay was silently dropped (Img2WebpHandler skips
// -d for negatives) so the frame got img2webp's 100 ms default instead.
class StreamingWebpWriterFrameDelayTest : FunSpec({

   val red = ImmutableImage.filled(4, 4, Color.RED)

   test("withFrameDelay rejects a zero delay") {
      shouldThrow<IllegalArgumentException> {
         StreamingWebpWriter().withFrameDelay(Duration.ZERO)
      }.message shouldContain "at least 1 ms"
   }

   test("withFrameDelay rejects a negative delay") {
      shouldThrow<IllegalArgumentException> {
         StreamingWebpWriter().withFrameDelay(Duration.ofMillis(-5))
      }.message shouldContain "at least 1 ms"
   }

   test("withFrameDelay rejects a null delay") {
      shouldThrow<IllegalArgumentException> {
         StreamingWebpWriter().withFrameDelay(null)
      }
   }

   test("withFrameDelay rejects a sub-millisecond delay") {
      shouldThrow<IllegalArgumentException> {
         StreamingWebpWriter().withFrameDelay(Duration.ofNanos(500_000))
      }
   }

   test("writeFrame rejects a zero per-frame delay") {
      val output = ByteArrayOutputStream()
      StreamingWebpWriter().prepareStream(output).use { stream ->
         stream.writeFrame(red)
         shouldThrow<IllegalArgumentException> {
            stream.writeFrame(red, Duration.ZERO)
         }.message shouldContain "at least 1 ms"
      }
   }

   test("writeFrame rejects a negative per-frame delay") {
      val output = ByteArrayOutputStream()
      StreamingWebpWriter().prepareStream(output).use { stream ->
         stream.writeFrame(red)
         shouldThrow<IllegalArgumentException> {
            stream.writeFrame(red, Duration.ofMillis(-5))
         }.message shouldContain "at least 1 ms"
      }
   }

   test("writeFrame rejects a null per-frame delay") {
      val output = ByteArrayOutputStream()
      StreamingWebpWriter().prepareStream(output).use { stream ->
         stream.writeFrame(red)
         shouldThrow<IllegalArgumentException> {
            stream.writeFrame(red, null)
         }
      }
   }

   test("a 1 ms delay is accepted") {
      val output = ByteArrayOutputStream()
      StreamingWebpWriter().withFrameDelay(Duration.ofMillis(1)).prepareStream(output).use { stream ->
         stream.writeFrame(red)
         stream.writeFrame(red, Duration.ofMillis(1))
      }
      (output.size() > 0) shouldBe true
   }
})
