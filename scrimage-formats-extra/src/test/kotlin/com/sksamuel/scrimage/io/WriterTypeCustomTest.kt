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
import java.awt.Transparency
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ComponentColorModel
import java.awt.image.DataBuffer
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

// ImageIO decodes 16-bit PNGs into TYPE_CUSTOM (type 0) images backed by a DataBufferUShort, and
// ImmutableImage.loader() hands them back as-is. The TwelveMonkeys TGA, TIFF, IFF and PICT
// encoders cannot encode those: TIFF threw IllegalArgumentException("Not implemented for data
// type: 1"), IFF ArrayIndexOutOfBoundsException, PICT IIOException("DataBuffer type 1 not
// supported") and TGA wrote a file no reader can decode.
class WriterTypeCustomTest : FunSpec({

   val writers: Map<String, ImageWriter> = mapOf(
      "BmpWriter" to BmpWriter(),
      "PnmWriter" to PnmWriter(),
      "TgaWriter" to TgaWriter(),
      "TiffWriter" to TiffWriter(),
      "IffWriter" to IffWriter(),
      "PictWriter" to PictWriter(),
   )

   // a 16-bit RGB(A) PNG, loaded through the normal loader
   fun load16bitPng(alpha: Boolean): ImmutableImage {
      val cm = ComponentColorModel(
         ColorSpace.getInstance(ColorSpace.CS_sRGB),
         alpha,
         false,
         if (alpha) Transparency.TRANSLUCENT else Transparency.OPAQUE,
         DataBuffer.TYPE_USHORT
      )
      val bi = BufferedImage(cm, cm.createCompatibleWritableRaster(12, 6), false, null)
      for (y in 0 until bi.height) for (x in 0 until bi.width) {
         bi.setRGB(x, y, (0xFF shl 24) or (((x * 37 + 11) and 0xFF) shl 16) or (((y * 53 + 101) and 0xFF) shl 8) or ((x * 17 + y * 29 + 201) and 0xFF))
      }
      val out = ByteArrayOutputStream()
      ImageIO.write(bi, "png", out)
      val image = ImmutableImage.loader().fromBytes(out.toByteArray())
      image.type shouldBe BufferedImage.TYPE_CUSTOM
      return image
   }

   for ((writerName, writer) in writers) {
      for (alpha in listOf(true, false)) {
         val label = if (alpha) "16-bit RGBA" else "16-bit RGB"
         test("$writerName round-trips a $label PNG (TYPE_CUSTOM)") {
            val src = load16bitPng(alpha)
            val back = ImmutableImage.loader().fromBytes(src.bytes(writer))
            back.width shouldBe src.width
            back.height shouldBe src.height
            back.argbints().toList() shouldBe src.argbints().toList()
         }
      }
   }
})
