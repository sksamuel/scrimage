package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage

/**
 * Regression test for rescaleInPlace on sub-raster views.
 *
 * rescaleInPlace ran RescaleOp.filter(awt, awt). For byte-interleaved images
 * (TYPE_3BYTE_BGR, TYPE_4BYTE_ABGR, TYPE_4BYTE_ABGR_PRE) that are a getSubimage view of a
 * larger image, the JDK's in-place RescaleOp uses the wrong sample offsets: channels inside
 * the view are shifted and the column just right of the view - outside it, in the caller's
 * parent image - is modified.
 */
class RescaleInPlaceSubRasterTest : FunSpec({

   for (type in listOf(
      BufferedImage.TYPE_3BYTE_BGR,
      BufferedImage.TYPE_4BYTE_ABGR,
      BufferedImage.TYPE_4BYTE_ABGR_PRE,
      BufferedImage.TYPE_INT_RGB,
      BufferedImage.TYPE_INT_ARGB,
   )) {
      test("rescaleInPlace on a getSubimage view of type $type only changes the view") {
         val parent = BufferedImage(12, 10, type)
         parent.setRGB(0, 0, 12, 10, IntArray(120) { 0xFF404040.toInt() }, 0, 12)
         ImmutableImage.wrapAwt(parent.getSubimage(3, 2, 5, 4)).rescaleInPlace(2.0)
         val expected = IntArray(120) { i ->
            val x = i % 12
            val y = i / 12
            if (x in 3..7 && y in 2..5) 0xFF808080.toInt() else 0xFF404040.toInt()
         }
         parent.getRGB(0, 0, 12, 10, null, 0, 12).map { "%08x".format(it) } shouldBe expected.map { "%08x".format(it) }
      }
   }
})
