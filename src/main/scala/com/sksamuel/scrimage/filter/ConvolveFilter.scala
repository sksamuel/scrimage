package com.sksamuel.scrimage.filter

import java.awt.image.Kernel

/** @author Stephen Samuel
  *
  *         A filter which applies a convolution kernel to an image.
  *
  *
  *         Copyright 2013 Stephen Samuel
  *         Based on original Java code copyright 2006 Jerry Huxtable
  *
  *         Licensed under the Apache License, Version 2.0 (the "License");
  *         you may not use this file except in compliance with the License.
  *         You may obtain a copy of the License at
  *
  *         http://www.apache.org/licenses/LICENSE-2.0
  *
  *         Unless required by applicable law or agreed to in writing, software
  *         distributed under the License is distributed on an "AS IS" BASIS,
  *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *         See the License for the specific language governing permissions and
  *         limitations under the License.
  *
  */
abstract class ConvolveFilter(kernel: Kernel) {

    /**
     * Treat pixels off the edge as zero.
     */
    var ZERO_EDGES: Int = 0
    /**
     * Clamp pixels off the edge to the nearest edge.
     */
    var CLAMP_EDGES: Int = 1
    /**
     * Wrap pixels off the edge to the opposite edge.
     */
    var WRAP_EDGES: Int = 2

    /**
     * Whether to convolve alpha.
     */
    var alpha = true

    /**
     * Whether to promultiply the alpha before convolving.
     */
    var premultiplyAlpha = true

    /**
     * Convolve a block of pixels.
     * @param kernel the kernel
     * @param inPixels the input pixels
     * @param outPixels the output pixels
     * @param width the width
     * @param height the height
     * @param edgeAction what to do at the edges
     */
    def convolve(kernel: Kernel, inPixels: Array[Int], outPixels: Array[Int], width: Int, height: Int, edgeAction: Int) {
        convolve(kernel, inPixels, outPixels, width, height, true, edgeAction)
    }
    /**
     * Convolve a block of pixels.
     * @param kernel the kernel
     * @param inPixels the input pixels
     * @param outPixels the output pixels
     * @param width the width
     * @param height the height
     * @param alpha include alpha channel
     * @param edgeAction what to do at the edges
     */
    def convolve(kernel: Kernel, inPixels: Array[Int], outPixels: Array[Int], width: Int, height: Int, alpha: Boolean, edgeAction: Int) {
        if (kernel.getHeight == 1) convolveH(kernel, inPixels, outPixels, width, height, alpha, edgeAction)
        else if (kernel.getWidth == 1) convolveV(kernel, inPixels, outPixels, width, height, alpha, edgeAction)
        else convolveHV(kernel, inPixels, outPixels, width, height, alpha, edgeAction)
    }
    /**
     * Convolve with a 2D kernel.
     * @param kernel the kernel
     * @param inPixels the input pixels
     * @param outPixels the output pixels
     * @param width the width
     * @param height the height
     * @param alpha include alpha channel
     * @param edgeAction what to do at the edges
     */
    def convolveHV(kernel: Kernel, inPixels: Array[Int], outPixels: Array[Int], width: Int, height: Int, alpha: Boolean, edgeAction: Int) {
        val index: Int = 0
        val matrix: Array[Float] = kernel.getKernelData(null)
        val rows: Int = kernel.getHeight
        val cols: Int = kernel.getWidth
        val rows2: Int = rows / 2
        val cols2: Int = cols / 2

    }
    /**
     * Convolve with a kernel consisting of one row.
     * @param kernel the kernel
     * @param inPixels the input pixels
     * @param outPixels the output pixels
     * @param width the width
     * @param height the height
     * @param alpha include alpha channel
     * @param edgeAction what to do at the edges
     */
    def convolveH(kernel: Kernel, inPixels: Array[Int], outPixels: Array[Int], width: Int, height: Int, alpha: Boolean, edgeAction: Int) {
        val index: Int = 0
        val matrix: Array[Float] = kernel.getKernelData(null)
        val cols: Int = kernel.getWidth
        val cols2: Int = cols / 2
    }

    /**
     * Convolve with a kernel consisting of one column.
     * @param kernel the kernel
     * @param inPixels the input pixels
     * @param outPixels the output pixels
     * @param width the width
     * @param height the height
     * @param alpha include alpha channel
     * @param edgeAction what to do at the edges
     */
    def convolveV(kernel: Kernel, inPixels: Array[Int], outPixels: Array[Int], width: Int, height: Int, alpha: Boolean, edgeAction: Int) {
        val index: Int = 0
        val matrix: Array[Float] = kernel.getKernelData(null)
        val rows: Int = kernel.getHeight
        val rows2: Int = rows / 2
    }
}