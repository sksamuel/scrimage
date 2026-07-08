package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.ScaleMethod
import com.sksamuel.scrimage.angles.Degrees
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Transparency
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ComponentColorModel
import java.awt.image.DataBuffer

/**
 * Regression tests for operations on TYPE_CUSTOM (type 0) images.
 *
 * The JDK PNG reader decodes 16-bit/channel PNGs to a TYPE_CUSTOM BufferedImage,
 * and the default loader preserves the type. rotate() and nearest-neighbour scaling
 * passed getType() straight into the BufferedImage constructor, which throws
 * "IllegalArgumentException: Unknown image type 0". blank()/empty()/subimage()/fromAwt()
 * already special-case type 0; these two call sites were missed.
 */
class TypeCustomOpsTest : FunSpec({

   // a 16-bit-per-channel RGB image, reporting BufferedImage.TYPE_CUSTOM (0)
   fun custom16bit(w: Int, h: Int): BufferedImage {
      val cm = ComponentColorModel(
         ColorSpace.getInstance(ColorSpace.CS_sRGB),
         intArrayOf(16, 16, 16),
         false,
         false,
         Transparency.OPAQUE,
         DataBuffer.TYPE_USHORT
      )
      val image = BufferedImage(cm, cm.createCompatibleWritableRaster(w, h), false, null)
      for (y in 0 until h) for (x in 0 until w) image.setRGB(x, y, 0xFF336699.toInt())
      return image
   }

   test("rotate works on a TYPE_CUSTOM image") {
      val awt = custom16bit(30, 20)
      awt.type shouldBe BufferedImage.TYPE_CUSTOM
      val rotated = ImmutableImage.wrapAwt(awt).rotate(Degrees(90))
      rotated.width shouldBe 20
      rotated.height shouldBe 30
      rotated.pixel(10, 15).toARGBInt() shouldBe 0xFF336699.toInt()
   }

   test("scaleTo with FastScale works on a TYPE_CUSTOM image") {
      val awt = custom16bit(30, 20)
      val scaled = ImmutableImage.wrapAwt(awt).scaleTo(10, 10, ScaleMethod.FastScale)
      scaled.width shouldBe 10
      scaled.height shouldBe 10
      scaled.pixel(5, 5).toARGBInt() shouldBe 0xFF336699.toInt()
   }
})
