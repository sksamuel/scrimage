package com.sksamuel.scrimage.core.nio.internal

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.GifWriter
import com.sksamuel.scrimage.nio.internal.GifSequenceReader
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File

/**
 * Regression for [GifSequenceReader.read] (String) lower-casing the whole path before opening
 * it. The lower-casing was only meant to make the `file:` / `:/` URL scheme check case
 * insensitive, but the lower-cased string was then handed to FileInputStream / URL, so on a
 * case-sensitive filesystem a file called `MixedCase.GIF` could not be opened (STATUS_OPEN_ERROR).
 */
class GifSequenceReaderFilenameCaseTest : FunSpec({

   fun gifBytes() = ImmutableImage.filled(4, 4, Color.RED, BufferedImage.TYPE_INT_RGB).bytes(GifWriter.Default)

   test("read(String) keeps the case of a file path") {
      val dir = tempdir()
      val f = File(dir, "MixedCase.GIF")
      f.writeBytes(gifBytes())
      val reader = GifSequenceReader()
      reader.read(f.absolutePath) shouldBe GifSequenceReader.STATUS_OK
      reader.frameCount shouldBe 1
   }

   test("read(String) keeps the case of a file: URL") {
      val dir = tempdir()
      val f = File(dir, "MixedCase.GIF")
      f.writeBytes(gifBytes())
      val reader = GifSequenceReader()
      reader.read(f.toURI().toURL().toString()) shouldBe GifSequenceReader.STATUS_OK
      reader.frameCount shouldBe 1
   }

   test("read(String) still accepts an upper-case FILE: scheme") {
      val dir = tempdir()
      val f = File(dir, "MixedCase.GIF")
      f.writeBytes(gifBytes())
      val reader = GifSequenceReader()
      val url = f.toURI().toURL().toString().replaceFirst("file:", "FILE:")
      reader.read(url) shouldBe GifSequenceReader.STATUS_OK
      reader.frameCount shouldBe 1
   }

   test("read(String) reports STATUS_OPEN_ERROR for a missing file") {
      val dir = tempdir()
      GifSequenceReader().read(File(dir, "Missing.GIF").absolutePath) shouldBe GifSequenceReader.STATUS_OPEN_ERROR
   }
})
