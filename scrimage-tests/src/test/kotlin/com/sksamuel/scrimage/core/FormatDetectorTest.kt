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

   // Regression: detect(byte[]) compares magic bytes with a ranged Arrays.equals
   // prefix check, which must not read past the end of short inputs. These
   // verify short/truncated inputs return empty rather than throwing.
   "detect(byte[]) with short input" should {
      "return empty for an empty array" {
         FormatDetector.detect(ByteArray(0)).isPresent shouldBe false
      }
      "return empty for 1-3 byte prefixes of magic numbers" {
         FormatDetector.detect(byteArrayOf(0x89.toByte())).isPresent shouldBe false
         FormatDetector.detect(byteArrayOf('G'.code.toByte(), 'I'.code.toByte())).isPresent shouldBe false
         FormatDetector.detect(byteArrayOf(0xFF.toByte(), 0xD8.toByte())).isPresent shouldBe false
         FormatDetector.detect(byteArrayOf('R'.code.toByte(), 'I'.code.toByte(), 'F'.code.toByte())).isPresent shouldBe false
      }
      "detect gif from exactly the 4 byte magic" {
         FormatDetector.detect(byteArrayOf('G'.code.toByte(), 'I'.code.toByte(), 'F'.code.toByte(), '8'.code.toByte()))
            .get() shouldBe Format.GIF
      }
      "detect jpeg from exactly the 3 byte magic" {
         FormatDetector.detect(byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte())).get() shouldBe Format.JPEG
      }
      "detect png from exactly the 8 byte magic" {
         val png = byteArrayOf(0x89.toByte(), 'P'.code.toByte(), 'N'.code.toByte(), 'G'.code.toByte(), 0x0D, 0x0A, 0x1A, 0x0A)
         FormatDetector.detect(png).get() shouldBe Format.PNG
      }
      "return empty for 8-11 byte RIFF headers too short to contain the WEBP tag" {
         // RIFF + partial length/WEBP tag: isWebp must check length before
         // comparing bytes 8..12 or the ranged equals would throw.
         val riff = byteArrayOf('R'.code.toByte(), 'I'.code.toByte(), 'F'.code.toByte(), 'F'.code.toByte())
         for (extra in 4..7) {
            FormatDetector.detect(riff + ByteArray(extra)).isPresent shouldBe false
         }
      }
      "detect webp from exactly a 12 byte RIFF/WEBP header" {
         val header = byteArrayOf(
            'R'.code.toByte(), 'I'.code.toByte(), 'F'.code.toByte(), 'F'.code.toByte(),
            0x1A, 0x2B, 0x3C, 0x4D, // arbitrary riff chunk length
            'W'.code.toByte(), 'E'.code.toByte(), 'B'.code.toByte(), 'P'.code.toByte(),
         )
         FormatDetector.detect(header).get() shouldBe Format.WEBP
      }
      "return empty for a RIFF header without the WEBP tag" {
         val header = byteArrayOf(
            'R'.code.toByte(), 'I'.code.toByte(), 'F'.code.toByte(), 'F'.code.toByte(),
            0, 0, 0, 0,
            'W'.code.toByte(), 'A'.code.toByte(), 'V'.code.toByte(), 'E'.code.toByte(),
         )
         FormatDetector.detect(header).isPresent shouldBe false
      }
      "return empty for unknown bytes" {
         FormatDetector.detect(ByteArray(16) { 0x42 }).isPresent shouldBe false
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
