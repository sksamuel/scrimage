package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.ImageIOReader
import com.sksamuel.scrimage.nio.ImmutableImageLoader
import com.sksamuel.scrimage.nio.PngReader
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.util.zip.CRC32
import java.util.zip.Deflater

/**
 * Regression for PngReader (the pngj fallback reader) ignoring the tRNS colour-key chunk on
 * truecolor (colour type 2) and greyscale (colour type 0) PNGs. tRNS names one sample value or
 * RGB triple that is fully transparent; PngReader honoured tRNS only for indexed PNGs and
 * emitted alpha 255 for every truecolor / greyscale pixel. ImageIO honours the key.
 *
 * The key is stored at the file's native bit depth, so the comparison has to use the raw
 * samples: the 16-bit cases below pick a key that differs from an opaque pixel only in the
 * low byte, and the 4-bit case exercises the pre-scaleUp path.
 */
class PngReaderColorKeyTransparencyTest : FunSpec({

   /**
    * Builds a minimal PNG. [rows] holds one int per sample (channels * width per row) at
    * the given bit depth; [trns] is the raw tRNS payload.
    */
   fun png(w: Int, h: Int, bitDepth: Int, colorType: Int, channels: Int, rows: List<IntArray>, trns: ByteArray): ByteArray {
      val out = ByteArrayOutputStream()
      val dos = DataOutputStream(out)
      dos.write(byteArrayOf(0x89.toByte(), 'P'.code.toByte(), 'N'.code.toByte(), 'G'.code.toByte(), 0x0D, 0x0A, 0x1A, 0x0A))
      fun chunk(type: String, data: ByteArray) {
         dos.writeInt(data.size)
         val tb = type.toByteArray(Charsets.US_ASCII)
         dos.write(tb)
         dos.write(data)
         val crc = CRC32()
         crc.update(tb)
         crc.update(data)
         dos.writeInt(crc.value.toInt())
      }
      val ihdr = ByteArrayOutputStream().also { b ->
         val d = DataOutputStream(b)
         d.writeInt(w); d.writeInt(h); d.writeByte(bitDepth); d.writeByte(colorType); d.writeByte(0); d.writeByte(0); d.writeByte(0)
      }.toByteArray()
      chunk("IHDR", ihdr)
      chunk("tRNS", trns)
      val raw = ByteArrayOutputStream()
      for (row in rows) {
         raw.write(0) // filter: none
         row.size shouldBe w * channels
         when (bitDepth) {
            16 -> for (s in row) { raw.write((s shr 8) and 0xff); raw.write(s and 0xff) }
            8 -> for (s in row) raw.write(s and 0xff)
            else -> { // sub-byte greyscale, packed msb first
               var acc = 0; var nbits = 0
               for (s in row) {
                  acc = (acc shl bitDepth) or (s and ((1 shl bitDepth) - 1)); nbits += bitDepth
                  if (nbits == 8) { raw.write(acc); acc = 0; nbits = 0 }
               }
               if (nbits > 0) raw.write(acc shl (8 - nbits))
            }
         }
      }
      val def = Deflater()
      def.setInput(raw.toByteArray())
      def.finish()
      val buf = ByteArray(4096)
      val idat = ByteArrayOutputStream()
      while (!def.finished()) { val n = def.deflate(buf); idat.write(buf, 0, n) }
      chunk("IDAT", idat.toByteArray())
      chunk("IEND", ByteArray(0))
      return out.toByteArray()
   }

   fun be16(vararg values: Int): ByteArray =
      values.flatMap { listOf(((it shr 8) and 0xff).toByte(), (it and 0xff).toByte()) }.toByteArray()

   fun viaPngj(bytes: ByteArray): ImmutableImage =
      ImmutableImageLoader.create().withImageReaders(listOf(PngReader())).fromBytes(bytes)

   fun alphaMatches(reference: ImmutableImage, other: ImmutableImage) {
      // the alpha of every pixel must agree with the JDK reader (colour is not compared: ImageIO
      // converts greyscale PNGs from CS_GRAY to sRGB when reading ARGB, pngj returns the raw sample)
      for (y in 0 until reference.height) for (x in 0 until reference.width) {
         other.pixel(x, y).alpha() shouldBe reference.pixel(x, y).alpha()
      }
   }

   test("8-bit truecolor PNG: the tRNS colour key is transparent, other pixels opaque") {
      val bytes = png(2, 1, 8, 2, 3, listOf(intArrayOf(255, 0, 0, 0, 0, 255)), be16(255, 0, 0))
      val reference = ImmutableImageLoader.create().withImageReaders(listOf(ImageIOReader())).fromBytes(bytes)
      reference.pixel(0, 0).alpha() shouldBe 0 // sanity: ImageIO honours the key
      val img = viaPngj(bytes)
      img.pixel(0, 0).alpha() shouldBe 0
      img.pixel(1, 0).alpha() shouldBe 255
      img.pixel(1, 0).toARGBInt() shouldBe 0xFF0000FF.toInt()
      alphaMatches(reference, img)
   }

   test("16-bit truecolor PNG: the key is compared on raw 16-bit samples, not the high byte") {
      // pixel 0 is exactly the key; pixel 1 differs from the key only in the low byte of red
      val key = intArrayOf(0x8001, 0x0000, 0x0000)
      val near = intArrayOf(0x8002, 0x0000, 0x0000)
      val bytes = png(2, 1, 16, 2, 3, listOf(key + near), be16(0x8001, 0x0000, 0x0000))
      val img = viaPngj(bytes)
      img.pixel(0, 0).alpha() shouldBe 0
      img.pixel(1, 0).alpha() shouldBe 255
      img.pixel(1, 0).red() shouldBe 0x80
   }

   test("8-bit greyscale PNG: the tRNS grey key is transparent, other pixels opaque") {
      val bytes = png(3, 1, 8, 0, 1, listOf(intArrayOf(10, 200, 10)), be16(10))
      val reference = ImmutableImageLoader.create().withImageReaders(listOf(ImageIOReader())).fromBytes(bytes)
      reference.pixel(0, 0).alpha() shouldBe 0
      val img = viaPngj(bytes)
      img.pixel(0, 0).alpha() shouldBe 0
      img.pixel(1, 0).alpha() shouldBe 255
      img.pixel(1, 0).red() shouldBe 200
      img.pixel(2, 0).alpha() shouldBe 0
      alphaMatches(reference, img)
   }

   test("4-bit greyscale PNG: the key is compared before samples are scaled up") {
      // samples 0..15; key = 5
      val bytes = png(4, 1, 4, 0, 1, listOf(intArrayOf(5, 15, 0, 5)), be16(5))
      val img = viaPngj(bytes)
      img.pixel(0, 0).alpha() shouldBe 0
      img.pixel(1, 0).alpha() shouldBe 255
      img.pixel(1, 0).red() shouldBe 255
      img.pixel(2, 0).alpha() shouldBe 255
      img.pixel(2, 0).red() shouldBe 0
      img.pixel(3, 0).alpha() shouldBe 0
   }

   test("16-bit greyscale PNG: the key is compared on raw 16-bit samples") {
      val bytes = png(2, 1, 16, 0, 1, listOf(intArrayOf(0x4001, 0x4002)), be16(0x4001))
      val img = viaPngj(bytes)
      img.pixel(0, 0).alpha() shouldBe 0
      img.pixel(1, 0).alpha() shouldBe 255
      img.pixel(1, 0).red() shouldBe 0x40
   }
})
