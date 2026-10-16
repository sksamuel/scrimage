package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.JpegWriter
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.string.shouldContain
import java.awt.Color
import java.awt.image.BufferedImage

/**
 * Regression for JpegWriter silently accepting a compression outside 0..100 (and the -1
 * "copy from metadata" sentinel). io.md documents compression as a value between 0 and 100 and
 * PngWriter validates its range up front, but JpegWriter.compression(101) raised nothing and
 * fell into the MODE_COPY_FROM_METADATA branch, encoding at the JDK default quality (0.75)
 * instead of the quality the caller asked for.
 */
class JpegWriterValidationTest : FunSpec({

   test("compression above 100 is rejected") {
      val e = shouldThrow<IllegalArgumentException> { JpegWriter.compression(101) }
      e.message shouldContain "101"
      shouldThrow<IllegalArgumentException> { JpegWriter(1000, false) }
      shouldThrow<IllegalArgumentException> { JpegWriter().withCompression(101) }
   }

   test("compression below -1 is rejected") {
      shouldThrow<IllegalArgumentException> { JpegWriter.compression(-2) }
      shouldThrow<IllegalArgumentException> { JpegWriter().withCompression(Int.MIN_VALUE) }
   }

   test("0..100 and the -1 sentinel are accepted") {
      shouldNotThrowAny {
         for (q in 0..100) JpegWriter.compression(q)
         JpegWriter.compression(-1)
         JpegWriter().withCompression(-1)
      }
      // the predefined writers still initialise
      JpegWriter.CompressionFromMetaData
      JpegWriter.Default
      @Suppress("DEPRECATION")
      JpegWriter.NoCompression
   }

   test("-1 still copies quality from metadata and writes a JPEG") {
      val img = ImmutableImage.filled(32, 32, Color.RED, BufferedImage.TYPE_INT_RGB)
      img.bytes(JpegWriter.compression(-1)).size shouldBeGreaterThan 2
   }
})
