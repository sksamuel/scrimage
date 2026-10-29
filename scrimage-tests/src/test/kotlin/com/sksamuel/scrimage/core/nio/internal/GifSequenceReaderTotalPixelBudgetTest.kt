package com.sksamuel.scrimage.core.nio.internal

import com.sksamuel.scrimage.nio.AnimatedGifReader
import com.sksamuel.scrimage.nio.ImageSource
import com.sksamuel.scrimage.nio.internal.GifSequenceReader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe
import java.io.ByteArrayInputStream
import java.io.IOException

/**
 * The decompression bomb checks from #905 bound the logical screen and each frame on their
 * own, but every frame is materialised as a full logical-screen TYPE_INT_ARGB image. A few
 * hundred bytes of 1x1 frames on a large (but individually acceptable) logical screen
 * therefore still demanded gigabytes, and AnimatedGifReader.read threw OutOfMemoryError
 * instead of the IOException it promises for malformed input.
 */
class GifSequenceReaderTotalPixelBudgetTest : FunSpec({

   fun le(value: Int): ByteArray = byteArrayOf((value and 0xff).toByte(), (value shr 8 and 0xff).toByte())

   // a well formed GIF89a whose frames are all 1x1, about 15 bytes each
   fun gif(screenWidth: Int, screenHeight: Int, frames: Int): ByteArray {
      var bytes = "GIF89a".toByteArray(Charsets.US_ASCII) +
         le(screenWidth) + le(screenHeight) +
         byteArrayOf(0x80.toByte(), 0x00, 0x00) +          // global colour table flag, 2 entries
         byteArrayOf(0, 0, 0, -1, -1, -1)                  // the global colour table
      repeat(frames) {
         bytes += byteArrayOf(0x2C) +                      // image separator
            le(0) + le(0) + le(1) + le(1) +
            byteArrayOf(0x00) +                            // packed: no local colour table
            byteArrayOf(0x02) +                            // LZW minimum code size
            byteArrayOf(0x02, 0x44, 0x01, 0x00)            // clear, index 0, end of information
      }
      return bytes + byteArrayOf(0x3B)
   }

   test("a few hundred bytes of 1x1 frames on a large screen are rejected, not decoded until OOM") {
      // 8000x6000 is under DEFAULT_MAX_PIXELS, so each frame alone is acceptable, but each
      // one costs 192MB; on master the third frame exhausted the default test heap.
      val bomb = gif(8000, 6000, 30)
      bomb.size shouldBeLessThan 500
      shouldThrow<IOException> { AnimatedGifReader.read(ImageSource.of(bomb)) }
   }

   test("the total budget counts the logical screen of every frame") {
      val reader = GifSequenceReader()
      reader.maxPixels = 1_000_000
      // budget = 1,000,000 + ~470 bytes * 4096 < 3 frames of 1000x1000
      reader.read(ByteArrayInputStream(gif(1000, 1000, 30))) shouldBe GifSequenceReader.STATUS_FORMAT_ERROR
   }

   test("an explicit maxTotalPixels overrides the derived budget") {
      val reader = GifSequenceReader()
      reader.maxTotalPixels = 30L * 1000 * 1000
      reader.read(ByteArrayInputStream(gif(1000, 1000, 30))) shouldBe GifSequenceReader.STATUS_OK
      reader.frameCount shouldBe 30

      val strict = GifSequenceReader()
      strict.maxTotalPixels = 5L * 10 * 10
      strict.read(ByteArrayInputStream(gif(10, 10, 6))) shouldBe GifSequenceReader.STATUS_FORMAT_ERROR
   }

   test("many small frames on a small screen still decode with the default budget") {
      val reader = GifSequenceReader()
      reader.read(ByteArrayInputStream(gif(100, 100, 200))) shouldBe GifSequenceReader.STATUS_OK
      reader.frameCount shouldBe 200
   }

   test("maxTotalPixels defaults to derived and rejects a negative override") {
      val reader = GifSequenceReader()
      reader.maxTotalPixels shouldBe 0L
      shouldThrow<IllegalArgumentException> { reader.maxTotalPixels = -1 }
   }
})
