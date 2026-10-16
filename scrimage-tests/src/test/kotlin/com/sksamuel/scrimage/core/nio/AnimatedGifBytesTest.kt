package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.DisposeMethod
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.AnimatedGifReader
import com.sksamuel.scrimage.nio.ImageSource
import com.sksamuel.scrimage.nio.StreamingGifWriter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.time.Duration

/**
 * Regression for [com.sksamuel.scrimage.nio.AnimatedGif.getBytes] re-encoding the GIF through
 * GifSequenceWriter instead of returning the bytes that were read. Re-encoding forced every
 * frame's disposal method to "none" and applied frame 0's delay to all frames, so a GIF with
 * mixed disposal or mixed delays did not survive `read -> getBytes -> read`.
 */
class AnimatedGifBytesTest : FunSpec({

   fun mixedGif(): ByteArray {
      val out = ByteArrayOutputStream()
      StreamingGifWriter(Duration.ofMillis(100), true, false).prepareStream(out, BufferedImage.TYPE_INT_ARGB).use { s ->
         s.writeFrame(ImmutableImage.filled(4, 4, Color.RED), Duration.ofMillis(100), DisposeMethod.RESTORE_TO_BACKGROUND_COLOR)
         s.writeFrame(ImmutableImage.filled(4, 4, Color.GREEN), Duration.ofMillis(500), DisposeMethod.DO_NOT_DISPOSE)
      }
      return out.toByteArray()
   }

   test("getBytes returns the bytes that were read") {
      val original = mixedGif()
      val gif = AnimatedGifReader.read(ImageSource.of(original))
      gif.bytes shouldBe original
   }

   test("getBytes returns a copy, not the internal array") {
      val gif = AnimatedGifReader.read(ImageSource.of(mixedGif()))
      val a = gif.bytes
      val b = gif.bytes
      a shouldNotBeSameInstanceAs b
      a[0] = 0
      gif.bytes[0] shouldNotBe 0.toByte()
   }

   test("getBytes preserves per-frame disposal methods") {
      val gif = AnimatedGifReader.read(ImageSource.of(mixedGif()))
      gif.getDisposeMethod(0) shouldBe DisposeMethod.RESTORE_TO_BACKGROUND_COLOR
      gif.getDisposeMethod(1) shouldBe DisposeMethod.DO_NOT_DISPOSE
      val again = AnimatedGifReader.read(ImageSource.of(gif.bytes))
      again.frameCount shouldBe 2
      again.getDisposeMethod(0) shouldBe DisposeMethod.RESTORE_TO_BACKGROUND_COLOR
      again.getDisposeMethod(1) shouldBe DisposeMethod.DO_NOT_DISPOSE
   }

   test("getBytes preserves per-frame delays") {
      val gif = AnimatedGifReader.read(ImageSource.of(mixedGif()))
      gif.getDelay(0) shouldBe Duration.ofMillis(100)
      gif.getDelay(1) shouldBe Duration.ofMillis(500)
      val again = AnimatedGifReader.read(ImageSource.of(gif.bytes))
      again.getDelay(0) shouldBe Duration.ofMillis(100)
      again.getDelay(1) shouldBe Duration.ofMillis(500)
   }
})
