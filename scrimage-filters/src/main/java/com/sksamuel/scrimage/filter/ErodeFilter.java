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
     * @param iterations the number of erosion passes; higher values erode more.
     */
    public ErodeFilter(int iterations) {
        this.iterations = iterations;
    }

    @Override
    public BufferedImageOp op() {
        thirdparty.jhlabs.image.ErodeFilter filter = new thirdparty.jhlabs.image.ErodeFilter();
        filter.setIterations(iterations);
        return filter;
    }
}
