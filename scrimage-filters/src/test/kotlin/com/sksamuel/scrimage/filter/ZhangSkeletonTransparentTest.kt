package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Regression: ZhangSkeleton binarised on brightness alone, and the brightness
 * of a fully transparent pixel (0x00000000) is 0, so an entirely transparent
 * image was treated as one solid dark shape and thinned to a single black
 * pixel. Fully transparent pixels are now background; any pixel with a
 * non-zero alpha is still classified by brightness as before.
 */
class ZhangSkeletonTransparentTest : FunSpec({

   fun blackCount(image: ImmutableImage): Int {
      var c = 0
      for (y in 0 until image.height)
         for (x in 0 until image.width)
            if (image.pixel(x, y).red() < 128) c++
      return c
   }

   test("a fully transparent image yields no skeleton") {
      val out = ImmutableImage.create(10, 10).filter(ZhangSkeleton())
      blackCount(out) shouldBe 0
   }

   test("a dark shape on a transparent background skeletonises exactly like on a white background") {
      // 3-row thick black bar in rows 2..4 of a 9x7 image
      val onTransparent = ImmutableImage.create(9, 7)
      val onWhite = ImmutableImage.filled(9, 7, Color.WHITE)
      for (y in 2..4) for (x in 0 until 9) {
         onTransparent.awt().setRGB(x, y, 0xff000000.toInt())
         onWhite.awt().setRGB(x, y, 0xff000000.toInt())
      }
      val out = onTransparent.filter(ZhangSkeleton())
      out shouldBe onWhite.filter(ZhangSkeleton())
      // the transparent rows are background (white) in the output and the bar survives
      for (x in 0 until 9) for (y in listOf(0, 1, 5, 6)) (out.pixel(x, y).red() < 128) shouldBe false
      blackCount(out) shouldBeGreaterThan 0
   }

   test("a semi-transparent dark pixel is still foreground") {
      val img = ImmutableImage.create(5, 5)
      img.awt().setRGB(2, 2, 0x80000000.toInt())
      val out = img.filter(ZhangSkeleton())
      (out.pixel(2, 2).red() < 128) shouldBe true
      blackCount(out) shouldBe 1
   }
})
