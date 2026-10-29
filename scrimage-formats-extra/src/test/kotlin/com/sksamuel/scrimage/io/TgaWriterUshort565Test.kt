package com.sksamuel.scrimage.io

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.TgaWriter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage

// The TwelveMonkeys TGA encoder wrote TYPE_USHORT_565_RGB samples verbatim into a 16-bit TGA,
// which the format defines as 1-5-5-5, so every colour came back scrambled (e.g. ff0865c5 was
// decoded as ff10cec5). WriterBufferedImageTypeTest did not catch this because its black/white
// fixture is the same in both bit layouts.
class TgaWriterUshort565Test : FunSpec({

   test("TgaWriter keeps the colours of TYPE_USHORT_565_RGB images") {
      val bi = BufferedImage(24, 12, BufferedImage.TYPE_USHORT_565_RGB)
      for (y in 0 until bi.height) for (x in 0 until bi.width) {
         val r = (x * 37 + 11) and 0xFF
         val g = (y * 53 + 101) and 0xFF
         val b = (x * 17 + y * 29 + 201) and 0xFF
         bi.setRGB(x, y, (0xFF shl 24) or (r shl 16) or (g shl 8) or b)
      }
      val src = ImmutableImage.wrapAwt(bi)
      val back = ImmutableImage.loader().fromBytes(src.bytes(TgaWriter()))
      back.width shouldBe src.width
      back.height shouldBe src.height
      // Normalising to TYPE_INT_RGB goes through Java2D, whose 5/6-bit to 8-bit expansion can
      // differ from getRGB's by one level; anything beyond that is a scrambled bit layout.
      val expected = src.argbints()
      val actual = back.argbints()
      val maxDelta = expected.indices.maxOf { i ->
         listOf(24, 16, 8, 0).maxOf { shift ->
            Math.abs(((expected[i] shr shift) and 0xFF) - ((actual[i] shr shift) and 0xFF))
         }
      }
      maxDelta shouldBeLessThanOrEqual 1
   }
})
