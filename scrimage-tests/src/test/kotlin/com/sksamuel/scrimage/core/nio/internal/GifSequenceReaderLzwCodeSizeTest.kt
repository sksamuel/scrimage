package com.sksamuel.scrimage.core.nio.internal

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.AnimatedGifReader
import com.sksamuel.scrimage.nio.GifWriter
import com.sksamuel.scrimage.nio.ImageSource
import com.sksamuel.scrimage.nio.internal.GifSequenceReader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.IOException

/**
 * Regression for a malformed LZW minimum code size escaping [AnimatedGifReader.read] as an
 * ArrayIndexOutOfBoundsException. GIF LZW codes are at most 12 bits wide, so the minimum code
 * size must be at most 11 (the spec allows 2..8). decodeImageData sized its string tables at
 * 4096 entries but initialised `1 << data_size` of them, so a code size of 12 or more indexed
 * past the end of the tables instead of reporting STATUS_FORMAT_ERROR.
 */
class GifSequenceReaderLzwCodeSizeTest : FunSpec({

   /** A valid single-frame GIF followed by the offset of its LZW minimum code size byte. */
   fun gifAndCodeSizeOffset(): Pair<ByteArray, Int> {
      val bytes = ImmutableImage.filled(4, 4, Color.RED, BufferedImage.TYPE_INT_RGB).bytes(GifWriter.Default)
      var p = 6 // header
      val packed = bytes[p + 4].toInt() and 0xff
      p += 7 // logical screen descriptor
      if (packed and 0x80 != 0) p += 3 * (2 shl (packed and 7)) // global colour table
      while (bytes[p].toInt() and 0xff == 0x21) { // skip extensions
         p += 2
         while (true) {
            val size = bytes[p].toInt() and 0xff
            p += 1 + size
            if (size == 0) break
         }
      }
      (bytes[p].toInt() and 0xff) shouldBe 0x2C // image descriptor
      val ipacked = bytes[p + 9].toInt() and 0xff
      p += 10
      if (ipacked and 0x80 != 0) p += 3 * (2 shl (ipacked and 7)) // local colour table
      return bytes to p
   }

   test("sanity: the unmodified fixture decodes") {
      val (bytes, _) = gifAndCodeSizeOffset()
      AnimatedGifReader.read(ImageSource.of(bytes)).frameCount shouldBe 1
   }

   for (codeSize in listOf(12, 13, 255)) {
      test("LZW minimum code size $codeSize is reported as a format error, not an ArrayIndexOutOfBoundsException") {
         val (bytes, offset) = gifAndCodeSizeOffset()
         bytes[offset] = codeSize.toByte()
         shouldThrow<IOException> { AnimatedGifReader.read(ImageSource.of(bytes)) }
         val reader = GifSequenceReader()
         reader.read(ByteArrayInputStream(bytes)) shouldBe GifSequenceReader.STATUS_FORMAT_ERROR
      }
   }
})
