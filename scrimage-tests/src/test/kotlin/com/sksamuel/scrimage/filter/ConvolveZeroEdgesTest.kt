package com.sksamuel.scrimage.filter

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe
import thirdparty.jhlabs.image.ConvolveFilter
import thirdparty.jhlabs.image.GaussianFilter
import java.awt.image.Kernel

/**
 * The static convolution helpers handled CLAMP_EDGES and WRAP_EDGES at the image
 * borders but, for ZERO_EDGES, left the sample index out of range and indexed the
 * pixel array anyway, throwing ArrayIndexOutOfBoundsException. (convolveHV already
 * skipped out-of-bounds samples correctly.) ZERO_EDGES means out-of-bounds
 * neighbours contribute nothing, so the border samples should simply be skipped.
 */
class ConvolveZeroEdgesTest : FunSpec({

   val gray = 0xff808080.toInt()
   val red = { rgb: Int -> (rgb shr 16) and 0xff }

   // a horizontal gaussian kernel (width=5, height=1) and its vertical form (width=1, height=5)
   val hKernel = GaussianFilter.makeKernel(2f)
   val vKernel = Kernel(1, hKernel.width, hKernel.getKernelData(null))

   test("convolveAndTranspose does not overrun the array with ZERO_EDGES") {
      val width = 5; val height = 1
      val inPix = IntArray(width * height) { gray }
      val out = IntArray(width * height)
      GaussianFilter.convolveAndTranspose(hKernel, inPix, out, width, height, true, false, false, ConvolveFilter.ZERO_EDGES)
      // out is transposed to height x width; for height==1 index is just x
      red(out[2]) shouldBe 128            // centre unaffected
      red(out[0]) shouldBeLessThan 128    // edge pulled toward the zero background
   }

   test("convolveH does not overrun the array with ZERO_EDGES") {
      val width = 5; val height = 1
      val inPix = IntArray(width * height) { gray }
      val out = IntArray(width * height)
      ConvolveFilter.convolveH(hKernel, inPix, out, width, height, true, ConvolveFilter.ZERO_EDGES)
      red(out[2]) shouldBe 128
      red(out[0]) shouldBeLessThan 128
   }

   test("convolveV does not overrun the array with ZERO_EDGES") {
      val width = 1; val height = 5
      val inPix = IntArray(width * height) { gray }
      val out = IntArray(width * height)
      ConvolveFilter.convolveV(vKernel, inPix, out, width, height, true, ConvolveFilter.ZERO_EDGES)
      red(out[2]) shouldBe 128
      red(out[0]) shouldBeLessThan 128
   }
})
