package com.sksamuel.scrimage.core.nio.internal

import com.sksamuel.scrimage.nio.AnimatedGifReader
import com.sksamuel.scrimage.nio.ImageSource
import com.sksamuel.scrimage.nio.internal.GifSequenceReader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.FunSpec
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.Base64

/**
 * Regression for a GIF decompression bomb (issue #893): a 35 byte file that declares
 * huge dimensions forced GifSequenceReader into multi gigabyte allocations, because
 * both `new byte[iw * ih]` in decodeImageData and `new BufferedImage(width, height)`
 * in readImage were sized straight from the header with no validation. The result was
 * an OutOfMemoryError (or, for 65535x65535, a NegativeArraySizeException from the
 * overflowed int pixel count) thrown out of AnimatedGifReader.read, killing the JVM
 * rather than reporting a malformed file.
 */
class GifSequenceReaderDecompressionBombTest : FunSpec({

   fun le(value: Int): ByteArray = byteArrayOf((value and 0xff).toByte(), (value shr 8 and 0xff).toByte())

   /**
    * A minimal, well formed GIF89a with a two entry global colour table and a single
    * frame whose LZW data is a handful of bytes, so nothing in the file backs up the
    * dimensions it declares.
    */
   fun gif(screenWidth: Int, screenHeight: Int, frameWidth: Int, frameHeight: Int): ByteArray =
      "GIF89a".toByteArray(Charsets.US_ASCII) +
         le(screenWidth) + le(screenHeight) +
         byteArrayOf(0x80.toByte(), 0x00, 0x00) +          // global colour table flag, 2 entries
         byteArrayOf(0, 0, 0, -1, -1, -1) +                // the global colour table
         byteArrayOf(0x2C) +                               // image separator
         le(0) + le(0) + le(frameWidth) + le(frameHeight) +
         byteArrayOf(0x00) +                               // packed: no local colour table, no interlace
         byteArrayOf(0x02) +                               // LZW minimum code size
         byteArrayOf(0x02, 0x44, 0x01, 0x00) +             // one tiny data block, then the block terminator
         byteArrayOf(0x3B)                                 // trailer

   fun statusOf(bytes: ByteArray): Int = GifSequenceReader().read(ByteArrayInputStream(bytes))

   listOf(
      "huge screen and huge frame" to gif(65535, 32767, 65535, 32767),
      "huge screen, one pixel frame" to gif(65535, 32767, 1, 1),
      "one pixel screen, huge frame" to gif(1, 1, 65535, 32767),
      // 65535 * 65535 overflows an int to -131071, which used to reach new byte[-131071]
      "dimensions that overflow an int pixel count" to gif(65535, 65535, 65535, 65535),
   ).forEach { (description, bomb) ->
      test("a 35 byte gif is rejected rather than allocating for it: $description") {
         bomb.size shouldBe 35
         statusOf(bomb) shouldBe GifSequenceReader.STATUS_FORMAT_ERROR
         shouldThrow<IOException> { AnimatedGifReader.read(ImageSource.of(bomb)) }
      }
   }

   test("the proof of concept files from issue 893 are rejected") {
      // These declare no colour table at all, so they are already caught by the
      // colour table check, but they are what was reported so they are pinned here.
      listOf(
         "R0lGODn//x8fgAAAACH5BAAAAAAALAAAAAB//x8fAAA=",
         "R0lGODn//3//gAAAACH5BAAAAAAALAAAAAB///9/AAA=",
      ).forEach {
         val bomb = Base64.getDecoder().decode(it)
         statusOf(bomb) shouldBe GifSequenceReader.STATUS_FORMAT_ERROR
         shouldThrowAny { AnimatedGifReader.read(ImageSource.of(bomb)) }
      }
   }

   test("a stream that ends inside the logical screen descriptor is rejected") {
      // readShort returns -1 at end of stream, so width and height arrive negative.
      statusOf("GIF89a".toByteArray(Charsets.US_ASCII)) shouldBe GifSequenceReader.STATUS_FORMAT_ERROR
   }

   test("a zero sized logical screen is rejected") {
      statusOf(gif(0, 0, 1, 1)) shouldBe GifSequenceReader.STATUS_FORMAT_ERROR
   }

   test("a real animated gif still decodes") {
      val bytes = javaClass.getResourceAsStream("/com/sksamuel/scrimage/nio/animated_birds.gif")!!.readBytes()
      val reader = GifSequenceReader()
      reader.read(ByteArrayInputStream(bytes)) shouldBe GifSequenceReader.STATUS_OK
      reader.frameCount shouldBe 4
   }

   test("maxPixels defaults to the documented limit and rejects a non positive override") {
      val reader = GifSequenceReader()
      reader.maxPixels shouldBe GifSequenceReader.DEFAULT_MAX_PIXELS
      shouldThrow<IllegalArgumentException> { reader.maxPixels = 0 }
   }
})
