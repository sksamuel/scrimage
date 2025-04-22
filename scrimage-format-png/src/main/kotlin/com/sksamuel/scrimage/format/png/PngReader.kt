@file:Suppress("ArrayInDataClass", "PrivatePropertyName")

package com.sksamuel.scrimage.format.png

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.PixelTools
import java.awt.image.BufferedImage
import java.io.InputStream
import kotlin.math.floor
import kotlin.math.roundToInt

class PngReader {

   private val magicBytes = byteArrayOf(137.toByte(), 80, 78, 71, 13, 10, 26, 10)
   private val IDHR: ByteArray = charArrayOf('I', 'H', 'D', 'R').map { it.code.toByte() }.toByteArray()
   private val IDAT: ByteArray = charArrayOf('I', 'D', 'A', 'T').map { it.code.toByte() }.toByteArray()
   private val IEND: ByteArray = charArrayOf('I', 'E', 'N', 'D').map { it.code.toByte() }.toByteArray()
   private val iCCP: ByteArray = charArrayOf('i', 'C', 'C', 'P').map { it.code.toByte() }.toByteArray()

   fun read(input: InputStream): ImmutableImage {
      val sig: ByteArray = input.readNBytes(magicBytes.size)
      if (!sig.contentEquals(magicBytes)) {
         error(
            "Not a valid PNG file: Magic bytes differ: " +
               "Expected ${magicBytes.toList()} but was ${sig.toList()}"
         )
      }

      val header = readHeaderChunk(input)
      val awt = BufferedImage(header.width, header.height, BufferedImage.TYPE_INT_ARGB)

      val bpp = header.bitDepth / 8 * when (header.colorType) {
         ColorType.RGBTriple -> 3
         ColorType.RGBATriple -> 4
      }

      val chunks = mutableListOf<Chunk>()
      while (input.available() > 0) {
         readChunk(input)?.apply { chunks.add(this) }
      }

      println("${chunks.size} chunks ${chunks.map { it::class }}")

//      val iccp = chunks.filterIsInstance<ICCPChunk>().firstOrNull()
//      println(iccp)

      val idats = chunks.filterIsInstance<DataChunk>()
      println("${idats.size} DataChunks")

      val datastream: ByteArray = idats.map { it.data }.reduce { a, b -> a + b }
      println("Starting data stream ${datastream.size}")

      val decompressed = deflate(datastream)
      println("result = " + decompressed.size)

      var k = 0
      var x = 0
      var y = 0
      var filterType: FilterType = FilterType.None
      while (k < decompressed.size) {
         // filter byte is always first byte of the scanline
         if (x == 0) {
            filterType = when (val byte = decompressed[k].toInt()) {
               0 -> FilterType.None
               1 -> FilterType.Sub
               2 -> FilterType.Up
               3 -> FilterType.Average
               4 -> FilterType.Paeth
               else -> error("Unsupported filter type $byte")
            }
            k++
         }
         val argb = header.colorType.argb(k, header.width, decompressed, filterType, bpp, awt, x, y)
         awt.setRGB(x, y, argb)
         x++
         if (x == header.width) {
            x = 0
            y++
         }
         k += when (header.colorType) {
            ColorType.RGBTriple -> 3
            ColorType.RGBATriple -> 4
         }
      }
      println("Finishing datastream")
      return ImmutableImage.wrapAwt(awt)

//      val cm = ColorModel.getRGBdefault()
//      return ImmutableImage.fromAwt(BufferedImage(cm, awt.raster, cm.isAlphaPremultiplied, null))
   }

   private fun readHeaderChunk(input: InputStream): HeaderChunk {

      val len = input.readNBytes(4).toUInt()
      require(len == 13L) { "Headers must be 13 bytes" }

      val type = input.readNBytes(4)
      require(type.contentEquals(IDHR)) { "Type must be IDHR" }

      val width = input.readNBytes(4).toUInt().toInt()
      val height = input.readNBytes(4).toUInt().toInt()
      val bitDepth = input.read().toByte()
      println("bitDepth $bitDepth")

      val colorType = when (val c = input.read()) {
         2 -> ColorType.RGBTriple
         6 -> ColorType.RGBATriple
         else -> error("Unsupported colorType $c")
      }
      println("colorType $colorType")

      val filterMethod = input.read()
      if (filterMethod != 0)
         error("Unsupported filterMethod $filterMethod")

      val compressionMethod = input.read().toByte()
      println("compressionMethod $compressionMethod")

      val interlaceMethod = input.read().toByte()
      println("interlaceMethod $interlaceMethod")

      val crc = input.readNBytes(4)
      return HeaderChunk(width, height, bitDepth, colorType, compressionMethod, interlaceMethod, crc)
   }

   private fun readChunk(input: InputStream): Chunk? {
      val len = input.readNBytes(4).toUInt()
      val type = input.readNBytes(4)

      fun typeString(type: ByteArray) = type.map { it.toInt().toChar() }.toCharArray().concatToString()
      println("Type=" + typeString(type))

      return when {
         type.contentEquals(IDAT) -> readDataChunk(input, len)
         type.contentEquals(IEND) -> readEndChunk(input, len)
         type.contentEquals(iCCP) -> readIccpChunk(input, len)
//         type[3].and(b) == 0.toByte() -> error("Unsupported critical chunk ${typeString(type)}")
         else -> {
            println("Unsupported chunk type ${type.map { it.toInt().toChar() }.toCharArray().concatToString()}")
            input.readNBytes(len.toInt())
            input.readNBytes(4) // crc
            null
         }
      }
   }

   private fun readIccpChunk(input: InputStream, len: Long): ICCPChunk {
      val bytes = input.readNBytes(len.toInt())
      val name = bytes.takeWhile { it != 0.toByte() }.map { it.toInt().toChar() }.toCharArray().concatToString()
      val profile: ByteArray = bytes.drop(name.length + 2).toByteArray()
      val crc = input.readNBytes(4)
      return ICCPChunk(name, 0, profile, crc)
   }

   private fun readDataChunk(input: InputStream, len: Long): DataChunk {
      val data = input.readNBytes(len.toInt())
      val crc = input.readNBytes(4).toInt()
      return DataChunk(len, data, crc)
   }

   private fun readEndChunk(input: InputStream, len: Long): EndChunk {
      val crc = input.readNBytes(4).toUInt()
      return EndChunk(crc)
   }
}

fun ByteArray.toUInt(): Long {
   var i = 0L
   i = i or (this[0].toLong() and 0xFF)
   i = i.shl(8)
   i = i or (this[1].toLong() and 0xFF)
   i = i.shl(8)
   i = i or (this[2].toLong() and 0xFF)
   i = i.shl(8)
   i = i or (this[3].toLong() and 0xFF)
   return i
}

fun ByteArray.toInt(): Int {
   var i = 0
   i = i or (this[0].toInt() and 0xFF)
   i = i.shl(8)
   i = i or (this[1].toInt() and 0xFF)
   i = i.shl(8)
   i = i or (this[2].toInt() and 0xFF)
   i = i.shl(8)
   i = i or (this[3].toInt() and 0xFF)
   return i
}

sealed interface Chunk

/**
 * Bit depth restrictions for each color type are imposed to simplify
 * implementations and to prohibit combinations that do not compress well.
 *
 * Decoders must support all legal combinations of bit depth and color type. The allowed combinations are:
 *
 * Color    Allowed    Interpretation
 * Type    Bit Depths
 *
 * 0       1,2,4,8,16  Each pixel is a grayscale sample.
 * 2       8,16        Each pixel is an R,G,B triple.
 * 3       1,2,4,8     Each pixel is a palette index; a PLTE chunk must appear.
 * 4       8,16        Each pixel is a grayscale sample, followed by an alpha sample.
 * 6       8,16        Each pixel is an R,G,B triple, followed by an alpha sample.
 *
 */
data class HeaderChunk(
   val width: Int,
   val height: Int,
   val bitDepth: Byte,
   val colorType: ColorType,
   val compressionMethod: Byte,
   val interlaceMethod: Byte,
   val crc: ByteArray,
) : Chunk {
   init {
      require(bitDepth in byteArrayOf(1, 2, 4, 8, 16)) { "Invalid bitDepth: $bitDepth" }
   }
}

data class PaletteChunk(val entry: Long) : Chunk

data class EndChunk(val crc: Long) : Chunk

data class ICCPChunk(
   val name: String,
   val compressionMethod: Byte,
   val profile: ByteArray,
   val crc: ByteArray
) : Chunk

data class DataChunk(val length: Long, val data: ByteArray, val crc: Int) : Chunk

sealed interface ColorType {

   fun argb(
      k: Int,
      width: Int,
      bytes: ByteArray,
      filterType: FilterType,
      bpp: Int,
      awt: BufferedImage,
      x: Int,
      y: Int
   ): Int

   object RGBTriple : ColorType {
      override fun argb(
         k: Int,
         width: Int,
         bytes: ByteArray,
         filterType: FilterType,
         bpp: Int,
         awt: BufferedImage,
         x: Int,
         y: Int
      ): Int {
         return PixelTools.argb(
            255,
            filterType.sample(k, width, bytes, bpp, awt, x, y, PixelTools::red),
            filterType.sample(k + 1, width, bytes, bpp, awt, x, y, PixelTools::green),
            filterType.sample(k + 2, width, bytes, bpp, awt, x, y, PixelTools::blue)
         )
      }
   }

   object RGBATriple : ColorType {
      override fun argb(
         k: Int,
         width: Int,
         bytes: ByteArray,
         filterType: FilterType,
         bpp: Int,
         awt: BufferedImage,
         x: Int,
         y: Int
      ): Int {

         val alpha = filterType.sample(k + 3, width, bytes, bpp, awt, x, y, PixelTools::alpha)
         // micro optimization - alpha 0 makes other values irrelevant
//         if (alpha == 255) return 0

         return PixelTools.argb(
            alpha,
            filterType.sample(k, width, bytes, bpp, awt, x, y, PixelTools::red),
            filterType.sample(k + 1, width, bytes, bpp, awt, x, y, PixelTools::green),
            filterType.sample(k + 2, width, bytes, bpp, awt, x, y, PixelTools::blue)
         )
      }
   }
}

sealed interface FilterType {

   fun sample(
      k: Int,
      width: Int,
      bytes: ByteArray,
      bpp: Int,
      awt: BufferedImage,
      x: Int,
      y: Int,
      band: (Int) -> Int
   ): Int

   object None : FilterType {
      override fun sample(
         k: Int,
         width: Int,
         bytes: ByteArray,
         bpp: Int,
         awt: BufferedImage,
         x: Int,
         y: Int,
         band: (Int) -> Int
      ): Int =
         bytes[k].toInt().and(0xFF)
   }

   object Sub : FilterType {
      override fun sample(
         k: Int,
         width: Int,
         bytes: ByteArray,
         bpp: Int,
         awt: BufferedImage,
         x: Int,
         y: Int,
         band: (Int) -> Int
      ): Int {
         return when (x) {
            0 -> bytes[k].toInt().and(0xFF)
            else -> (bytes[k].toInt().and(0xFF) + band(awt.getRGB(x - 1, y)).and(0xFF)) % 256
         }
      }
   }

   object Up : FilterType {
      override fun sample(
         k: Int,
         width: Int,
         bytes: ByteArray,
         bpp: Int,
         awt: BufferedImage,
         x: Int,
         y: Int,
         band: (Int) -> Int
      ): Int =
         when (y) {
            0 -> bytes[k].toInt().and(0xFF)
            else -> (bytes[k].toInt().and(0xFF) + band(awt.getRGB(x, y - 1))) % 256
         }
   }

   object Average : FilterType {
      override fun sample(
         k: Int,
         width: Int,
         bytes: ByteArray,
         bpp: Int,
         awt: BufferedImage,
         x: Int,
         y: Int,
         band: (Int) -> Int
      ): Int {
         val x2 = if (x == 0) 0 else band(awt.getRGB(x - 1, y))
         val y2 = if (y == 0) 0 else band(awt.getRGB(x, y - 1))
         return (bytes[k].toInt().and(0xFF) + floor((x2 + y2) / 2.0).roundToInt()) % 256
      }
   }

   object Paeth : FilterType {
      override fun sample(
         k: Int,
         width: Int,
         bytes: ByteArray,
         bpp: Int,
         awt: BufferedImage,
         x: Int,
         y: Int,
         band: (Int) -> Int
      ): Int {
         val left = if (x == 0) 0 else band(awt.getRGB(x - 1, y))
         val up = if (y == 0) 0 else band(awt.getRGB(x, y - 1))
         val upleft = if (x == 0 || y == 0) 0 else band(awt.getRGB(x - 1, y - 1))
         return (bytes[k].toInt().and(0xFF) + PaethPredictor.predict(left, up, upleft)) % 256
      }
   }
}
