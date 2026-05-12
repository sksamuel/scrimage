package com.sksamuel.scrimage.core.nio.internal

import com.sksamuel.scrimage.nio.internal.GifSequenceReader
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.BufferedInputStream
import java.io.InputStream

/**
 * Regression for GifSequenceReader.read(InputStream) and read(BufferedInputStream)
 * NPEing on a null argument. Both methods correctly null-check at the top
 * and set STATUS_OPEN_ERROR, but the unconditional `is.close()` afterwards
 * threw NullPointerException (not caught by the IOException handler) so
 * callers got an NPE rather than the documented STATUS_OPEN_ERROR return.
 */
class GifSequenceReaderNullStreamTest : FunSpec({

   test("read(InputStream) returns STATUS_OPEN_ERROR on null instead of NPEing") {
      val reader = GifSequenceReader()
      val status: Int = reader.read(null as InputStream?)
      status shouldBe GifSequenceReader.STATUS_OPEN_ERROR
   }

   test("read(BufferedInputStream) returns STATUS_OPEN_ERROR on null instead of NPEing") {
      val reader = GifSequenceReader()
      val status: Int = reader.read(null as BufferedInputStream?)
      status shouldBe GifSequenceReader.STATUS_OPEN_ERROR
   }
})
