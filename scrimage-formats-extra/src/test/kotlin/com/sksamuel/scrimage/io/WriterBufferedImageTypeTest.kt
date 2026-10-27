package com.sksamuel.scrimage.io

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.BmpWriter
import com.sksamuel.scrimage.nio.IffWriter
import com.sksamuel.scrimage.nio.ImageWriter
import com.sksamuel.scrimage.nio.PictWriter
import com.sksamuel.scrimage.nio.PnmWriter
import com.sksamuel.scrimage.nio.TgaWriter
import com.sksamuel.scrimage.nio.TiffWriter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.awt.image.BufferedImage

// The TwelveMonkeys encoders only support a subset of the standard BufferedImage types.
// For the rest they either threw (AIOOBE, IllegalArgumentException "Unknown TupleType",
// IIOException "DataBuffer type 1 not supported", ...) or wrote files no reader can
// decode (PNM/TGA x TYPE_BYTE_BINARY, IFF x 16-bit RGB). The writers must normalise
// such images to an encodable type so that every standard type round-trips.
class WriterBufferedImageTypeTest : FunSpec({

   val writers: Map<String, ImageWriter> = mapOf(
      "BmpWriter" to BmpWriter(),
      "PnmWriter" to PnmWriter(),
      "TgaWriter" to TgaWriter(),
      "TiffWriter" to TiffWriter(),
      "IffWriter" to IffWriter(),
      "PictWriter" to PictWriter(),
   )

   val types = mapOf(
      "TYPE_INT_RGB" to BufferedImage.TYPE_INT_RGB,
      "TYPE_INT_ARGB" to BufferedImage.TYPE_INT_ARGB,
      "TYPE_INT_BGR" to BufferedImage.TYPE_INT_BGR,
      "TYPE_3BYTE_BGR" to BufferedImage.TYPE_3BYTE_BGR,
      "TYPE_4BYTE_ABGR" to BufferedImage.TYPE_4BYTE_ABGR,
      "TYPE_BYTE_GRAY" to BufferedImage.TYPE_BYTE_GRAY,
      "TYPE_USHORT_GRAY" to BufferedImage.TYPE_USHORT_GRAY,
      "TYPE_BYTE_BINARY" to BufferedImage.TYPE_BYTE_BINARY,
      "TYPE_BYTE_INDEXED" to BufferedImage.TYPE_BYTE_INDEXED,
      "TYPE_USHORT_565_RGB" to BufferedImage.TYPE_USHORT_565_RGB,
      "TYPE_USHORT_555_RGB" to BufferedImage.TYPE_USHORT_555_RGB,
   )

   // left half black, right half white: both are exactly representable in every type
   // (including 1-bit and gray), so the round trip can be asserted pixel-exactly
   fun source(type: Int): ImmutableImage {
      val bi = BufferedImage(20, 10, type)
      val g = bi.createGraphics()
      try {
         g.color = Color.WHITE
         g.fillRect(0, 0, 20, 10)
         g.color = Color.BLACK
         g.fillRect(0, 0, 10, 10)
      } finally {
         g.dispose()
      }
      return ImmutableImage.fromAwt(bi)
   }

   for ((writerName, writer) in writers) {
      for ((typeName, type) in types) {
         test("$writerName round-trips $typeName") {
            val src = source(type)
            src.pixel(2, 5).toARGBInt() shouldBe 0xFF000000.toInt()
            src.pixel(17, 5).toARGBInt() shouldBe 0xFFFFFFFF.toInt()
            val back = ImmutableImage.loader().fromBytes(src.bytes(writer))
            back.width shouldBe 20
            back.height shouldBe 10
            back.argbints() shouldBe src.argbints()
         }
      }
   }
})
