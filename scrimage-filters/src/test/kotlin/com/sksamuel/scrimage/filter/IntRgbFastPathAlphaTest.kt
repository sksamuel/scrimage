package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.awt.Color
import java.awt.image.BufferedImage

/**
 * Regression tests for the jhlabs AbstractBufferedImageOp.getRGB fast path, which read raw data
 * elements for TYPE_INT_RGB images. Those elements are 0x00RRGGBB so every alpha-aware filter saw
 * alpha 0: convolution based filters (Blur, Sharpen, MotionBlur) premultiplied by zero and produced an
 * all-black image, binary filters (Erode, Skeleton) silently did nothing and ContourFilter drew
 * black contours.
 */
class IntRgbFastPathAlphaTest : FunSpec({

   fun rgbOnly(image: ImmutableImage): List<Int> = image.argbints().map { it and 0xffffff }

   val rgb = ImmutableImage.loader().fromResource("/bird.jpg").scaleTo(200, 150).copy(BufferedImage.TYPE_INT_RGB)
   val argb = rgb.copy(BufferedImage.TYPE_INT_ARGB)

   test("BlurFilter on a TYPE_INT_RGB image is not all black") {
      val out = ImmutableImage.filled(40, 30, Color(200, 100, 50), BufferedImage.TYPE_INT_RGB).filter(BlurFilter())
      out.argbints().count { (it and 0xffffff) != 0 } shouldNotBe 0
   }

   test("BlurFilter on a TYPE_INT_RGB image matches the TYPE_INT_ARGB result") {
      rgbOnly(rgb.filter(BlurFilter())) shouldBe rgbOnly(argb.filter(BlurFilter()))
   }

   test("ErodeFilter on a TYPE_INT_RGB image erodes like on TYPE_INT_ARGB") {
      val src = ImmutableImage.filled(20, 20, Color.WHITE, BufferedImage.TYPE_INT_RGB).map { p ->
         if (p.x() in 5..14 && p.y() in 5..14) Color.BLACK else Color.WHITE
      }
      val out = src.filter(ErodeFilter())
      out shouldNotBe src
      rgbOnly(out) shouldBe rgbOnly(src.copy(BufferedImage.TYPE_INT_ARGB).filter(ErodeFilter()))
   }

   test("ContourFilter on a TYPE_INT_RGB image matches the TYPE_INT_ARGB result") {
      rgbOnly(rgb.filter(ContourFilter())) shouldBe rgbOnly(argb.filter(ContourFilter()))
   }
})
