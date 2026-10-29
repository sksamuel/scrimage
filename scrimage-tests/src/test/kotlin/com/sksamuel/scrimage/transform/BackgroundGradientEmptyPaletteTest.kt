package com.sksamuel.scrimage.transform

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Regression test for BackgroundGradient on images that quantize to fewer than two
 * dominant colours.
 *
 * quantize(2) ignores (near) white pixels, so an all-white image yields an empty
 * palette and a single-colour image yields one entry; BackgroundGradient indexed
 * dominant[0] and dominant[1] unguarded and threw ArrayIndexOutOfBoundsException.
 * DominantGradient and DominantGradientFilter already guard both cases.
 */
class BackgroundGradientEmptyPaletteTest : FunSpec({

   test("all-white image does not throw and produces a canvas of the requested size") {
      val white = ImmutableImage.filled(10, 10, Color.WHITE)
      val out = white.transform(BackgroundGradient(20, 20))
      out.width shouldBe 20
      out.height shouldBe 20
      // the input is still drawn in the centre
      out.pixel(10, 10).toARGBInt() shouldBe Color.WHITE.rgb
   }

   test("single-colour image does not throw and produces a canvas of the requested size") {
      val red = ImmutableImage.filled(10, 10, Color.RED)
      val out = red.transform(BackgroundGradient(20, 20))
      out.width shouldBe 20
      out.height shouldBe 20
      // the input is still drawn in the centre, and the background is opaque
      out.pixel(10, 10).toARGBInt() shouldBe Color.RED.rgb
      out.pixel(0, 0).alpha() shouldBe 255
   }

   test("two-colour image still produces a gradient between the dominant colours") {
      val img = ImmutableImage.filled(10, 10, Color.RED)
      val g = img.awt().createGraphics()
      g.color = Color.BLUE
      g.fillRect(0, 5, 10, 5)
      g.dispose()
      val out = img.transform(BackgroundGradient(20, 40))
      out.width shouldBe 20
      out.height shouldBe 40
      // top and bottom rows of the background are different colours
      (out.pixel(0, 0).toARGBInt() == out.pixel(0, 39).toARGBInt()) shouldBe false
   }
})
