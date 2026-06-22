package com.sksamuel.scrimage.core.nio.internal

import com.sksamuel.scrimage.nio.internal.GifSequenceReader
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.ByteArrayInputStream

/**
 * Regression for GifSequenceReader.readImage NPEing on a malformed GIF that has
 * no global colour table (GCTFlag = 0), no local colour table (LCTFlag = 0) and
 * the transparency flag set in a graphics control extension. The active colour
 * table `act` is then null, but the transparency block dereferenced
 * `act[transIndex]` before the null check ran, throwing a raw
 * NullPointerException out of read(...) instead of returning STATUS_FORMAT_ERROR.
 */
class GifSequenceReaderNullColorTableTest : FunSpec({

   // GIF89a, 1x1, no global colour table, a GCE with the transparency bit set,
   // then an image descriptor with no local colour table.
   val malformed = byteArrayOf(
      'G'.code.toByte(), 'I'.code.toByte(), 'F'.code.toByte(),
      '8'.code.toByte(), '9'.code.toByte(), 'a'.code.toByte(), // signature
      0x01, 0x00,             // logical screen width = 1
      0x01, 0x00,             // logical screen height = 1
      0x00,                   // packed: GCTFlag = 0 (no global colour table)
      0x00,                   // background colour index
      0x00,                   // pixel aspect ratio
      0x21, 0xF9.toByte(), 0x04, // graphic control extension introducer + label + block size
      0x01,                   // packed: transparency flag = 1
      0x00, 0x00,             // delay
      0x00,                   // transparent colour index
      0x00,                   // block terminator
      0x2C,                   // image separator
      0x00, 0x00, 0x00, 0x00, // image left, top
      0x01, 0x00, 0x01, 0x00, // image width = 1, height = 1
      0x00,                   // packed: LCTFlag = 0 (no local colour table)
      0x3B                    // trailer
   )

   test("readImage reports STATUS_FORMAT_ERROR instead of NPEing when no colour table is defined") {
      val reader = GifSequenceReader()
      val status: Int = reader.read(ByteArrayInputStream(malformed))
      status shouldBe GifSequenceReader.STATUS_FORMAT_ERROR
   }
})
