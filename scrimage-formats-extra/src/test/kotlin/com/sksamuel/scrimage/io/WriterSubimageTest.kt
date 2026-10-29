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
import java.awt.image.BufferedImage

// Several TwelveMonkeys encoders (PNM, TGA, TIFF, PICT) read the backing DataBuffer directly
// and ignore a raster's origin and parent stride, so a sub-image view created with
// BufferedImage.getSubimage was written as the top-left corner of its PARENT image.
class WriterSubimageTest : FunSpec({

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
      "TYPE_3BYTE_BGR" to BufferedImage.TYPE_3BYTE_BGR,
      "TYPE_4BYTE_ABGR" to BufferedImage.TYPE_4BYTE_ABGR,
      "TYPE_BYTE_INDEXED" to BufferedImage.TYPE_BYTE_INDEXED,
   )

   // opaque, with distinct red/green/blue per pixel so a wrong origin or stride shows up
   fun parent(type: Int): BufferedImage {
      val bi = BufferedImage(24, 12, type)
      for (y in 0 until bi.height) for (x in 0 until bi.width) {
         val r = (x * 37 + 11) and 0xFF
         val g = (y * 53 + 101) and 0xFF
         val b = (x * 17 + y * 29 + 201) and 0xFF
         bi.setRGB(x, y, (0xFF shl 24) or (r shl 16) or (g shl 8) or b)
      }
      return bi
   }

   for ((writerName, writer) in writers) {
      for ((typeName, type) in types) {
         test("$writerName writes the pixels of a $typeName sub-image view") {
            val src = ImmutableImage.wrapAwt(parent(type).getSubimage(3, 2, 14, 7))
            val back = ImmutableImage.loader().fromBytes(src.bytes(writer))
            back.width shouldBe 14
            back.height shouldBe 7
            back.argbints().toList() shouldBe src.argbints().toList()
         }
      }
   }
})
