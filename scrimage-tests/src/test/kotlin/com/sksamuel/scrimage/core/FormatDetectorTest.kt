package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.format.Format
import com.sksamuel.scrimage.format.FormatDetector
import com.sksamuel.scrimage.nio.ImmutableImageLoader
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.io.ByteArrayInputStream
import java.io.InputStream

class FormatDetectorTest : WordSpec({

   "format detector" should {
      "detect png"  {
         FormatDetector.detect(javaClass.getResourceAsStream("/com/sksamuel/scrimage/chip_pad.png")).get() shouldBe Format.PNG
      }
      "detect jpeg"  {
         FormatDetector.detect(javaClass.getResourceAsStream("/com/sksamuel/scrimage/bird.jpg")).get() shouldBe Format.JPEG
      }
      "detect gif"  {
         FormatDetector.detect(javaClass.getResourceAsStream("/com/sksamuel/scrimage/io/bird_compressed.gif")).get() shouldBe Format.GIF
      }
      "detect webp"  {
         FormatDetector.detect(javaClass.getResourceAsStream("/com/sksamuel/scrimage/landscape.webp")).get() shouldBe Format.WEBP
      }
   }

   "image loader" should {
      "output webp warning" {
         shouldThrowAny {
            ImmutableImageLoader.create().fromResource("/spacedock.webp")
         }.message shouldContain "Image parsing failed for WEBP"
      }
   }

   // Wraps a stream so each read() / read(byte[], int, int) call returns
   // at most `chunkSize` bytes. Mimics network or decompressed streams
   // that don't satisfy a 12-byte read in one shot.
   class DribbleInputStream(private val source: InputStream, private val chunkSize: Int) : InputStream() {
      override fun read(): Int = source.read()
      override fun read(b: ByteArray, off: Int, len: Int): Int =
         source.read(b, off, minOf(len, chunkSize))
   }

   // Regression: detect(InputStream) used in.read(byte[], 0, 12) which is
   // documented as "an attempt is made to read up to len bytes" — so it
   // can return fewer. WEBP signatures live at offset 8, so a partial read
   // of just the RIFF header silently mis-detected as no format.
   "detect(InputStream) handles partial reads (regression)" should {
      "detect WEBP from a stream that returns 4 bytes at a time" {
         val webpBytes = javaClass.getResourceAsStream("/com/sksamuel/scrimage/landscape.webp")!!.readBytes()
         val drip = DribbleInputStream(ByteArrayInputStream(webpBytes), 4)
         FormatDetector.detect(drip).get() shouldBe Format.WEBP
      }
      "detect JPEG from a stream that returns 1 byte at a time" {
         val jpegBytes = javaClass.getResourceAsStream("/com/sksamuel/scrimage/bird.jpg")!!.readBytes()
         val drip = DribbleInputStream(ByteArrayInputStream(jpegBytes), 1)
         FormatDetector.detect(drip).get() shouldBe Format.JPEG
      }
      "detect PNG from a stream that returns 3 bytes at a time" {
         val pngBytes = javaClass.getResourceAsStream("/com/sksamuel/scrimage/chip_pad.png")!!.readBytes()
         val drip = DribbleInputStream(ByteArrayInputStream(pngBytes), 3)
         FormatDetector.detect(drip).get() shouldBe Format.PNG
      }
   }

})
