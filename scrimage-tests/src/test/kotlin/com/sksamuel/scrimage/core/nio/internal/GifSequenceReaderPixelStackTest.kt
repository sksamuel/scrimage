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
 * Regression: the LZW decoder expands a code by walking its prefix chain onto pixelStack
 * (MaxStackSize + 1 entries) without a bound. Corrupt image data can make that chain cycle, so
 * the walk ran off the end of pixelStack and escaped AnimatedGifReader.read as an
 * ArrayIndexOutOfBoundsException ("Index 4097 out of bounds for length 4097") instead of an
 * IOException. This is separate from the LZW minimum code size check added in #934.
 */
class GifSequenceReaderPixelStackTest : FunSpec({

   // mixed_disposal.gif with two bytes of its compressed image data changed; found by mutation fuzzing
   fun corrupted(): ByteArray {
      val bytes = javaClass.getResourceAsStream("/com/sksamuel/scrimage/nio/mixed_disposal.gif")!!.readBytes()
      bytes[88] = 33
      bytes[150] = 18
      return bytes
   }

   test("the unmodified file decodes") {
      val bytes = javaClass.getResourceAsStream("/com/sksamuel/scrimage/nio/mixed_disposal.gif")!!.readBytes()
      GifSequenceReader().read(ByteArrayInputStream(bytes)) shouldBe GifSequenceReader.STATUS_OK
   }

   test("GifSequenceReader reports a format error when an LZW prefix chain overruns the pixel stack") {
      GifSequenceReader().read(ByteArrayInputStream(corrupted())) shouldBe GifSequenceReader.STATUS_FORMAT_ERROR
   }

   test("AnimatedGifReader throws IOException when an LZW prefix chain overruns the pixel stack") {
      shouldThrow<IOException> { AnimatedGifReader.read(ImageSource.of(corrupted())) }
   }
})
