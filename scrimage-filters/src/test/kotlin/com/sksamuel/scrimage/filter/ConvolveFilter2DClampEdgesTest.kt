package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Regression: with CLAMP_EDGES (the ConvolveFilter default), the jhlabs 2D
 * convolution (convolveHV, used for any kernel with both dimensions > 1)
 * clamped off-edge samples to the *centre* pixel (`ix = x`, `ioffset = y *
 * width`) instead of the nearest edge pixel, whereas the 1D paths (convolveH /
 * convolveV) clamp to 0 / width-1 as documented. The two paths therefore
 * disagreed for identical kernels once the kernel was wide enough (>= 5) to
 * reach past the edge by more than one pixel.
 */
class ConvolveFilter2DClampEdgesTest : FunSpec({

   // 8x1 row of distinct greys; non-zero at the start so clamping to the edge
   // pixel is distinguishable from contributing nothing.
   val greys = intArrayOf(10, 40, 80, 120, 160, 200, 240, 255)

   fun row(): ImmutableImage {
      val img = ImmutableImage.create(8, 1)
      for (x in 0 until 8) img.awt().setRGB(x, 0, Color(greys[x], greys[x], greys[x]).rgb)
      return img
   }

   fun column(): ImmutableImage {
      val img = ImmutableImage.create(1, 8)
      for (y in 0 until 8) img.awt().setRGB(0, y, Color(greys[y], greys[y], greys[y]).rgb)
      return img
   }

   test("a 2D kernel clamps off-edge columns to the nearest edge pixel, like the 1D kernel") {
      // kernel that samples only the pixel two to the left: out[x] = in[clamp(x - 2)]
      val k1d = FloatArray(5); k1d[0] = 1f
      val k2d = FloatArray(25); k2d[2 * 5 + 0] = 1f
      val out1d = row().filter(ConvolveFilter(5, 1, k1d)).argbints().map { it and 0xff }
      val out2d = row().filter(ConvolveFilter(5, 5, k2d)).argbints().map { it and 0xff }
      val expected = (0 until 8).map { greys[maxOf(0, it - 2)] }
      "1d=$out1d 2d=$out2d" shouldBe "1d=$expected 2d=$expected"
   }

   test("a 2D kernel clamps off-edge rows to the nearest edge pixel, like the 1D kernel") {
      // kernel that samples only the pixel two below: out[y] = in[clamp(y + 2)]
      val k1d = FloatArray(5); k1d[4] = 1f
      val k2d = FloatArray(25); k2d[4 * 5 + 2] = 1f
      val out1d = column().filter(ConvolveFilter(1, 5, k1d)).argbints().map { it and 0xff }
      val out2d = column().filter(ConvolveFilter(5, 5, k2d)).argbints().map { it and 0xff }
      val expected = (0 until 8).map { greys[minOf(7, it + 2)] }
      "1d=$out1d 2d=$out2d" shouldBe "1d=$expected 2d=$expected"
   }

   test("3x3 kernels reaching one pixel past the edge are unaffected") {
      // for a 3x3 kernel the centre pixel and the edge pixel coincide at the border,
      // so the fix must not change the output of the common 3x3 case
      val img = ImmutableImage.create(5, 5).map { p -> Color(p.x * 20 + 10, p.y * 20 + 10, 100) }
      val shiftLeft = floatArrayOf(1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f) // samples (x-1, y-1)
      val out = img.filter(ConvolveFilter(shiftLeft))
      for (y in 0 until 5) for (x in 0 until 5) {
         out.pixel(x, y).red() shouldBe img.pixel(maxOf(0, x - 1), maxOf(0, y - 1)).red()
      }
   }
})
