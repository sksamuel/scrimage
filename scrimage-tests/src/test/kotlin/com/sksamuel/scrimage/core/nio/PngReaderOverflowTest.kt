@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.nio.PngReader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.string.shouldContain
import java.io.ByteArrayOutputStream
import java.util.zip.CRC32

class PngReaderOverflowTest : WordSpec({

   fun ByteArrayOutputStream.writeInt(v: Int) {
      write((v ushr 24) and 0xFF)
      write((v ushr 16) and 0xFF)
      write((v ushr 8) and 0xFF)
      write(v and 0xFF)
   }

   fun ByteArrayOutputStream.writeChunk(type: String, data: ByteArray) {
      writeInt(data.size)
      val typeBytes = type.toByteArray(Charsets.US_ASCII)
      write(typeBytes)
      write(data)
      val crc = CRC32()
      crc.update(typeBytes)
      crc.update(data)
      writeInt(crc.value.toInt())
   }

   // builds a structurally complete PNG (IHDR + a small IDAT + IEND) whose IHDR declares the
   // given dimensions. The image data is not valid pixels for those dimensions, but that does
   // not matter: the size guard runs before any row is read.
   fun pngWithDimensions(width: Int, height: Int): ByteArray {
      val out = ByteArrayOutputStream()
      // PNG signature
      out.write(byteArrayOf(0x89.toByte(), 'P'.code.toByte(), 'N'.code.toByte(), 'G'.code.toByte(), 0x0D, 0x0A, 0x1A, 0x0A))

      val ihdr = ByteArrayOutputStream()
      ihdr.writeInt(width)
      ihdr.writeInt(height)
      ihdr.write(8)  // bit depth
      ihdr.write(6)  // colour type RGBA
      ihdr.write(0)  // compression
      ihdr.write(0)  // filter
      ihdr.write(0)  // interlace
      out.writeChunk("IHDR", ihdr.toByteArray())

      // a non-empty (but bogus) IDAT so the reader has data chunks to find, then IEND.
      out.writeChunk("IDAT", ByteArray(16))
      out.writeChunk("IEND", ByteArray(0))
      return out.toByteArray()
   }

   "PngReader" should {
      // 65536 * 65536 == 2^32, which wraps to 0 in int arithmetic, yielding an undersized buffer.
      "reject PNGs whose declared dimensions overflow int when multiplied" {
         val bytes = pngWithDimensions(65536, 65536)
         val ex = shouldThrow<IllegalArgumentException> {
            PngReader().read(bytes, null)
         }
         ex.message!! shouldContain "PNG too large"
      }
   }

})
