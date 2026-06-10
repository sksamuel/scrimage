@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.JpegWriter
import com.sksamuel.scrimage.nio.PngWriter
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.io.ByteArrayOutputStream

/**
 * Regression tests for WriteContext.bytes(), which sizes its ByteArrayOutputStream
 * from the image dimensions (clamped to 1KB-8MB). These exercise images on both
 * sides of the clamp to verify the output is unaffected by the initial buffer size.
 */
class WriteContextTest : WordSpec({

   // 2x2 = 4 pixels, well below the 1024 byte lower clamp; distinct pixel colours
   val small = ImmutableImage.create(2, 2).map { p -> Color(p.x * 100, p.y * 100, 50) }

   // 3000x3000 = 9M pixels, above the 8MB upper clamp
   val large = ImmutableImage.filled(3000, 3000, Color(10, 120, 230))

   "WriteContext.bytes" should {
      "round-trip a small image via png" {
         val bytes = small.bytes(PngWriter.NoCompression)
         val read = ImmutableImage.loader().fromBytes(bytes)
         read.width shouldBe 2
         read.height shouldBe 2
         read.pixels().toList() shouldBe small.pixels().toList()
      }
      "round-trip a larger-than-clamp image via png" {
         val bytes = large.bytes(PngWriter.MaxCompression)
         val read = ImmutableImage.loader().fromBytes(bytes)
         read.width shouldBe 3000
         read.height shouldBe 3000
         // solid fill: spot-check corners and centre rather than 9M pixels
         listOf(0 to 0, 2999 to 0, 0 to 2999, 2999 to 2999, 1500 to 1500).forEach { (x, y) ->
            read.pixel(x, y) shouldBe large.pixel(x, y)
         }
      }
      "produce a decodable jpeg with correct dimensions for a small image" {
         val bytes = small.bytes(JpegWriter())
         val read = ImmutableImage.loader().fromBytes(bytes)
         read.width shouldBe 2
         read.height shouldBe 2
      }
      "produce a decodable jpeg with correct dimensions for a larger-than-clamp image" {
         val bytes = large.bytes(JpegWriter())
         val read = ImmutableImage.loader().fromBytes(bytes)
         read.width shouldBe 3000
         read.height shouldBe 3000
      }
      "produce output identical to write(OutputStream) for png" {
         val streamed = ByteArrayOutputStream()
         small.forWriter(PngWriter.NoCompression).write(streamed)
         small.bytes(PngWriter.NoCompression).toList() shouldBe streamed.toByteArray().toList()

         val streamedLarge = ByteArrayOutputStream()
         large.forWriter(PngWriter.MaxCompression).write(streamedLarge)
         large.bytes(PngWriter.MaxCompression).toList() shouldBe streamedLarge.toByteArray().toList()
      }
      "produce output identical to write(OutputStream) for jpeg" {
         val streamed = ByteArrayOutputStream()
         small.forWriter(JpegWriter()).write(streamed)
         small.bytes(JpegWriter()).toList() shouldBe streamed.toByteArray().toList()
      }
   }

})
