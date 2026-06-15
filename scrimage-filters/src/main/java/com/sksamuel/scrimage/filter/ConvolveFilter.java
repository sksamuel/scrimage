/*
   Copyright 2013 Stephen K Samuel

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.sksamuel.scrimage.filter;

import java.awt.image.BufferedImageOp;
import java.awt.image.Kernel;

/**
 * Applies an arbitrary convolution kernel to an image — the building block for
 * sharpen, blur, edge-detection and emboss effects via a custom matrix. This is
 * the equivalent of filterous-2's {@code convolute(matrix)}.
 *
 * <p>The kernel is applied exactly as given (it is <b>not</b> renormalised), and
 * out-of-bounds neighbours at the image borders are clamped to the edge pixel.
 * Matrices are row-major.
 */
public class ConvolveFilter extends BufferedOpFilter {

    private final int kernelWidth;
    private final int kernelHeight;
    private final float[] matrix;

    /**
     * Creates a 3x3 convolution filter from a 9-element row-major matrix.
     *
     * @param matrix the nine kernel weights, row by row
     */
    public ConvolveFilter(float[] matrix) {
        this(3, 3, matrix);
    }

    /**
     * Creates a convolution filter from a {@code kernelWidth} x {@code kernelHeight}
     * row-major matrix.
     *
     * @param kernelWidth  the number of columns in the kernel
     * @param kernelHeight the number of rows in the kernel
     * @param matrix       the kernel weights, row by row (length must be width * height)
     */
    public ConvolveFilter(int kernelWidth, int kernelHeight, float[] matrix) {
        if (kernelWidth <= 0 || kernelHeight <= 0)
            throw new IllegalArgumentException("kernel dimensions must be positive");
        if (matrix == null || matrix.length != kernelWidth * kernelHeight)
            throw new IllegalArgumentException(
                "matrix length must be kernelWidth * kernelHeight (" + (kernelWidth * kernelHeight) + ")");
        this.kernelWidth = kernelWidth;
        this.kernelHeight = kernelHeight;
        this.matrix = matrix.clone();
    }

    @Override
    public BufferedImageOp op() {
        return new thirdparty.jhlabs.image.ConvolveFilter(new Kernel(kernelWidth, kernelHeight, matrix));
    }
}
