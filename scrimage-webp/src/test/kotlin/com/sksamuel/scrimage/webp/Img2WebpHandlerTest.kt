package com.sksamuel.scrimage.webp

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PngWriter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import java.awt.Color

class Img2WebpHandlerTest : FunSpec({

   val handler = Img2WebpHandler()

   // Two distinct synthetic frames, encoded as PNG. img2webp accepts PNG/JPEG/TIFF/PNM/WebP.
   val frames: List<ByteArray> = listOf(
      ImmutableImage.filled(32, 16, Color.RED).bytes(PngWriter.NoCompression),
      ImmutableImage.filled(32, 16, Color.BLUE).bytes(PngWriter.NoCompression),
   )

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

   test("Img2WebpHandler.convert produces an animated WebP with default options") {
      val out = handler.convert(frames, -1, -1, -1, -1, false)
      isAnimatedWebp(out).shouldBeTrue()
   }

   test("Img2WebpHandler.convert honours explicit frame delay, loop, q, m and lossless") {
      val out = handler.convert(frames, 80, 2, 75, 4, true)
      isAnimatedWebp(out).shouldBeTrue()
   }

   test("Img2WebpHandler.convert rejects an empty frame list") {
      try {
         handler.convert(emptyList(), -1, -1, -1, -1, false)
         throw AssertionError("expected IllegalArgumentException")
      } catch (e: IllegalArgumentException) {
         (e.message?.contains("frame") == true) shouldBe true
      }
   }
})
