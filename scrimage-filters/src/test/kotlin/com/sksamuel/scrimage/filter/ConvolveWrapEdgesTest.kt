package com.sksamuel.scrimage.filter

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.image.Kernel
import thirdparty.jhlabs.image.ConvolveFilter.WRAP_EDGES

/**
 * Regression: with WRAP_EDGES the jhlabs GaussianFilter.convolveAndTranspose
 * and ConvolveFilter.convolveH / convolveV / convolveHV computed the wrapped
 * index from the *centre* coordinate (`(x + width) % width`, which is just x)
 * instead of the out-of-range sample coordinate, so an off-edge tap sampled
 * the centre pixel again rather than the pixel on the opposite edge. A 3-tap
 * box blur over the row [255, 0, 0, 0] must give (0 + 255 + 0) / 3 = 85 at
 * both ends; it gave (255 + 255 + 0) / 3 = 170 at pixel 0.
 */
class ConvolveWrapEdgesTest : FunSpec({

   val row = intArrayOf(0xff0000ff.toInt(), 0xff000000.toInt(), 0xff000000.toInt(), 0xff000000.toInt())
   val box3 = floatArrayOf(1 / 3f, 1 / 3f, 1 / 3f)

   fun blue(v: Int) = v and 0xff

   test("GaussianFilter.convolveAndTranspose wraps to the opposite edge") {
      val out = IntArray(4)
      thirdparty.jhlabs.image.GaussianFilter.convolveAndTranspose(
         Kernel(3, 1, box3), row, out, 4, 1, true, false, false, WRAP_EDGES
      )
      // height is 1 so the transposed layout is the same as the input layout
      out.map { blue(it) } shouldBe listOf(85, 85, 0, 85)
   }

   test("ConvolveFilter.convolveH wraps to the opposite edge") {
      val out = IntArray(4)
      thirdparty.jhlabs.image.ConvolveFilter.convolve(Kernel(3, 1, box3), row, out, 4, 1, WRAP_EDGES)
      out.map { blue(it) } shouldBe listOf(85, 85, 0, 85)
   }

   test("ConvolveFilter.convolveV wraps to the opposite edge") {
      // the same data as a 1x4 column
      val out = IntArray(4)
      thirdparty.jhlabs.image.ConvolveFilter.convolve(Kernel(1, 3, box3), row, out, 1, 4, WRAP_EDGES)
      out.map { blue(it) } shouldBe listOf(85, 85, 0, 85)
   }

   test("ConvolveFilter.convolveHV wraps columns to the opposite edge") {
      // 3x3 box on a 4x1 image: every row tap wraps onto the single row, so the
      // result equals the horizontal 3-tap box
      val out = IntArray(4)
      val box3x3 = FloatArray(9) { 1 / 9f }
      thirdparty.jhlabs.image.ConvolveFilter.convolve(Kernel(3, 3, box3x3), row, out, 4, 1, WRAP_EDGES)
      out.map { blue(it) } shouldBe listOf(85, 85, 0, 85)
   }

   test("WRAP_EDGES with a kernel wider than the image does not throw") {
      val tiny = intArrayOf(0xff0000ff.toInt(), 0xff000000.toInt())
      val out = IntArray(2)
      val box7 = FloatArray(7) { 1 / 7f }
      thirdparty.jhlabs.image.GaussianFilter.convolveAndTranspose(Kernel(7, 1, box7), tiny, out, 2, 1, true, false, false, WRAP_EDGES)
      thirdparty.jhlabs.image.ConvolveFilter.convolve(Kernel(7, 1, box7), tiny, out, 2, 1, WRAP_EDGES)
      thirdparty.jhlabs.image.ConvolveFilter.convolve(Kernel(1, 7, box7), tiny, out, 1, 2, WRAP_EDGES)
      // 7 taps (-3..3) over a 2-pixel ring: pixel 0 samples itself at taps -2, 0, 2
      // (255 three times) and pixel 1 at the four odd taps (0 four times)
      blue(out[0]) shouldBe (255 * 3 / 7f + 0.5f).toInt()
   }
})
