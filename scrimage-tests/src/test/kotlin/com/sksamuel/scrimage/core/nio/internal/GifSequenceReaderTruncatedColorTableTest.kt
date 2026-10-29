package com.sksamuel.scrimage.core.nio.internal

import com.sksamuel.scrimage.nio.AnimatedGifReader
import com.sksamuel.scrimage.nio.ImageSource
import com.sksamuel.scrimage.nio.internal.GifSequenceReader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.ByteArrayInputStream
import java.io.IOException

/**
 * Regression: readHeader read the global colour table and immediately dereferenced it
 * (`bgColor = gct[bgIndex]`). readColorTable returns null when the file ends inside the table,
 * so a truncated GIF (or one whose packed field declares a larger table than is present)
 * escaped AnimatedGifReader.read as a NullPointerException instead of an IOException.
 */
class GifSequenceReaderTruncatedColorTableTest : FunSpec({

   // GIF89a header and logical screen descriptor declaring a 4-entry global colour table,
   // followed by only one of the four entries.
   val truncated = byteArrayOf(
      'G'.code.toByte(), 'I'.code.toByte(), 'F'.code.toByte(),
      '8'.code.toByte(), '9'.code.toByte(), 'a'.code.toByte(), // signature
      0x02, 0x00,              // logical screen width = 2
      0x02, 0x00,              // logical screen height = 2
      0x81.toByte(),           // packed: GCTFlag = 1, size = 2 << 1 = 4 entries
      0x00,                    // background colour index
      0x00,                    // pixel aspect ratio
      0x10, 0x20, 0x30,        // first colour table entry; the file ends here
   )

   test("GifSequenceReader reports a format error for a truncated global colour table") {
      GifSequenceReader().read(ByteArrayInputStream(truncated)) shouldBe GifSequenceReader.STATUS_FORMAT_ERROR
   }

   test("AnimatedGifReader throws IOException for a truncated global colour table") {
      shouldThrow<IOException> { AnimatedGifReader.read(ImageSource.of(truncated)) }
   }

   test("AnimatedGifReader throws IOException when the file ends right after the screen descriptor") {
      shouldThrow<IOException> { AnimatedGifReader.read(ImageSource.of(truncated.copyOf(13))) }
   }
})
