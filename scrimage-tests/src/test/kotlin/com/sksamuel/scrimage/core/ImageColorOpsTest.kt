package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.LumaGrayscale
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for the ImmutableImage colour convenience methods: invert, gamma and the
 * no-arg toGrayscale.
 *
 * All tests use opaque alpha (255): copy() routes through Graphics2D which
 * premultiplies and loses precision when alpha != 255 (a pre-existing behaviour
 * shared with toGrayscale(GrayscaleMethod)).
 */
class ImageColorOpsTest : FunSpec({

   fun image1x1(r: Int, g: Int, b: Int, a: Int = 255) =
      ImmutableImage.create(1, 1, arrayOf(Pixel(0, 0, r, g, b, a)))

   test("invert flips RGB and preserves alpha") {
      val p = image1x1(10, 20, 30, 255).invert().pixel(0, 0)
      p.red() shouldBe 245
      p.green() shouldBe 235
      p.blue() shouldBe 225
      p.alpha() shouldBe 255
   }

   test("invert leaves the source image unchanged") {
      val src = image1x1(10, 20, 30, 255)
      src.invert()
      src.pixel(0, 0).red() shouldBe 10
   }

   test("gamma of 1.0 leaves pixels unchanged") {
      val p = image1x1(64, 128, 200, 255).gamma(1.0).pixel(0, 0)
      p.red() shouldBe 64
      p.green() shouldBe 128
      p.blue() shouldBe 200
      p.alpha() shouldBe 255
   }

   test("gamma of 2.0 brightens midtones per the jhlabs formula") {
      // v = round(255 * (64/255)^(1/2)) = round(127.75 + 0.5) = 128
      image1x1(64, 64, 64, 255).gamma(2.0).pixel(0, 0).red() shouldBe 128
   }

   test("no-arg toGrayscale matches the LUMA method") {
      val src = image1x1(100, 150, 200, 255)
      src.toGrayscale().pixel(0, 0).red() shouldBe src.toGrayscale(LumaGrayscale()).pixel(0, 0).red()
      // LumaGrayscale: round(0.2126*100 + 0.7152*150 + 0.0722*200) = 143
      src.toGrayscale().pixel(0, 0).red() shouldBe 143
   }
})
