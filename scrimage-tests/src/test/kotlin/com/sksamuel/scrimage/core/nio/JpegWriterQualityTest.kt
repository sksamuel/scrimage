package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.JpegWriter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.comparables.shouldBeGreaterThan
import java.awt.Color
import java.io.ByteArrayOutputStream

/**
 * Regression: JpegWriter mapped `compression == 100` to
 * ImageWriteParam.MODE_DISABLED — invalid for JPEG (which is always
 * compressed). The JDK JPEG writer throws or silently misbehaves for
 * this mode. A caller passing "100" expecting "best quality" got an
 * UnsupportedOperationException or a corrupt write at runtime.
 *
 * The fix maps the full [0, 100] range to MODE_EXPLICIT with
 * quality = compression / 100f, so 100 means "quality 1.0" — the
 * highest quality the codec produces.
 */
class JpegWriterQualityTest : FunSpec({

   fun source(): ImmutableImage =
      ImmutableImage.filled(64, 64, Color.RED)

   test("JpegWriter with compression=100 writes a JPEG successfully (highest quality)") {
      val bytes = ByteArrayOutputStream().use { out ->
         JpegWriter(100, false).write(source(), source().metadata, out)
         out.toByteArray()
      }
      // Smoke check: the bytes begin with the JPEG SOI marker FF D8.
      bytes.size shouldBeGreaterThan 2
      bytes[0].toInt() and 0xFF shouldBe 0xFF
      bytes[1].toInt() and 0xFF shouldBe 0xD8
   }

   test("JpegWriter with compression=80 still writes") {
      val bytes = ByteArrayOutputStream().use { out ->
         JpegWriter(80, false).write(source(), source().metadata, out)
         out.toByteArray()
      }
      bytes.size shouldBeGreaterThan 2
   }

   test("compression=100 output is larger than compression=10 (more quality, more bytes)") {
      val src = ImmutableImage.create(64, 64).map { p -> Color(p.x() * 4 % 256, p.y() * 4 % 256, 128) }
      val high = ByteArrayOutputStream().also { JpegWriter(100, false).write(src, src.metadata, it) }.toByteArray()
      val low = ByteArrayOutputStream().also { JpegWriter(10, false).write(src, src.metadata, it) }.toByteArray()
      high.size shouldBeGreaterThan low.size
   }
})
