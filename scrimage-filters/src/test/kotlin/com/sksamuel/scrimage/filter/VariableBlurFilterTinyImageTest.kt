package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import thirdparty.jhlabs.image.VariableBlurFilter
import java.awt.Color
import java.awt.image.BufferedImage

/**
 * Regression: VariableBlurFilter.blur() crashed with
 * ArrayIndexOutOfBoundsException (index -1) when the dimension being
 * blurred was a single pixel and the blur radius at that pixel was > 0.
 * The right-edge clamp computed the last pixel value from the prefix
 * sums as a[l] - a[l-1] with l = width-1 = 0, reading a[-1].
 * Since blur() runs once per axis, both 1-pixel-wide and 1-pixel-tall
 * images crashed.
 */
class VariableBlurFilterTinyImageTest : FunSpec({

   // a blur mask of the same dimensions, fully white, so that the blur
   // radius is maximal at every pixel (the default blurRadiusAt ramps
   // from 0, which masks the bug at x == 0)
   fun filterFor(width: Int, height: Int): VariableBlurFilter {
      val mask = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
      val g = mask.createGraphics()
      g.color = Color.WHITE
      g.fillRect(0, 0, width, height)
      g.dispose()
      val filter = VariableBlurFilter()
      filter.radius = 2
      filter.blurMask = mask
      return filter
   }

   fun image(width: Int, height: Int): ImmutableImage =
      ImmutableImage.filled(width, height, Color.ORANGE)

   test("variable blur of a 1x1 image completes and preserves dimensions") {
      val result = image(1, 1).op(filterFor(1, 1))
      result.width shouldBe 1
      result.height shouldBe 1
   }

   test("variable blur of a 1xN image completes and preserves dimensions") {
      val result = image(1, 7).op(filterFor(1, 7))
      result.width shouldBe 1
      result.height shouldBe 7
   }

   test("variable blur of an Nx1 image completes and preserves dimensions") {
      val result = image(7, 1).op(filterFor(7, 1))
      result.width shouldBe 7
      result.height shouldBe 1
   }

   test("variable blur of a 2x2 image completes and preserves dimensions") {
      val result = image(2, 2).op(filterFor(2, 2))
      result.width shouldBe 2
      result.height shouldBe 2
   }

   test("variable blur of a normal-size image is deterministic") {
      val source = ImmutableImage.loader().fromResource("/bird_small.png")
      val first = source.op(filterFor(source.width, source.height))
      val second = source.op(filterFor(source.width, source.height))
      first.pixels().map { it.argb } shouldBe second.pixels().map { it.argb }
   }
})
