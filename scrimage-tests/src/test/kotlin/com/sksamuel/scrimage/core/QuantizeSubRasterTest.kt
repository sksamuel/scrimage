package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage

/**
 * Regression test: ColorThief's byte fast path (TYPE_3BYTE_BGR / TYPE_4BYTE_ABGR)
 * threw IllegalArgumentException "(expectedDataLength = N) != (pixels.length = M)"
 * whenever the raster's DataBuffer was larger than width*height*depth, which is the
 * case for a sub-image view created with BufferedImage.getSubimage. The int fast
 * path already fell back to the slow getRGB path in that situation; the byte path
 * must do the same.
 */
class QuantizeSubRasterTest : FunSpec({

   // 20x20 parent whose central 10x10 region is: top half blue, bottom half red.
   // The surrounding border is green so that a wrong read (e.g. reading the parent
   // buffer directly) would surface a different palette.
   fun parent(type: Int): BufferedImage {
      val bi = BufferedImage(20, 20, type)
      for (y in 0 until 20) {
         for (x in 0 until 20) {
            val inner = x in 2..11 && y in 2..11
            val rgb = when {
               !inner -> 0xFF00FF00.toInt()
               y < 7 -> 0xFF0000FF.toInt()
               else -> 0xFFFF0000.toInt()
            }
            bi.setRGB(x, y, rgb)
         }
      }
      return bi
   }

   fun standalone(type: Int): BufferedImage {
      val bi = BufferedImage(10, 10, type)
      for (y in 0 until 10) for (x in 0 until 10) bi.setRGB(x, y, if (y < 5) 0xFF0000FF.toInt() else 0xFFFF0000.toInt())
      return bi
   }

   listOf(BufferedImage.TYPE_3BYTE_BGR to "TYPE_3BYTE_BGR", BufferedImage.TYPE_4BYTE_ABGR to "TYPE_4BYTE_ABGR").forEach { (type, name) ->
      test("quantize on a $name sub-raster view does not throw and matches the standalone image") {
         val sub = ImmutableImage.wrapAwt(parent(type).getSubimage(2, 2, 10, 10))
         val expected = ImmutableImage.wrapAwt(standalone(type)).quantize(2).map { it.toHex() }
         expected.shouldNotBeEmpty()
         sub.quantize(2).map { it.toHex() } shouldBe expected
      }
   }
})
