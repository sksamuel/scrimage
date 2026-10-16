package com.sksamuel.scrimage.filter;

import java.awt.image.BufferedImageOp;

/**
 * Performs binary erosion, setting all removed pixels to the given 'new' colour.
 * The number of erosion iterations controls how aggressive the effect is.
 * Wraps the JH Labs ErodeFilter.
 */
public class ErodeFilter extends BufferedOpFilter {

    private final int iterations;

    /**
     * Creates an ErodeFilter with a single erosion iteration.
     */
    public ErodeFilter() {
        this(1);
    }

    /**
     * @param iterations the number of erosion passes (at least 1); higher values erode more.
     * @throws IllegalArgumentException if iterations is less than 1
     */
    public ErodeFilter(int iterations) {
        // The jhlabs implementation only writes the output buffer inside the
        // iterations loop, so iterations <= 0 silently returned a fully
        // transparent black image rather than the unchanged input.
        if (iterations < 1)
            throw new IllegalArgumentException("iterations must be >= 1, got " + iterations);
        this.iterations = iterations;
    }

    @Override
    public BufferedImageOp op() {
        thirdparty.jhlabs.image.ErodeFilter filter = new thirdparty.jhlabs.image.ErodeFilter();
        filter.setIterations(iterations);
        return filter;
    }
}
