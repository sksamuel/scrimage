package com.sksamuel.scrimage.filter;

import java.awt.image.BufferedImageOp;

/**
 * Performs binary erosion, setting all removed pixels to the given 'new' colour.
 * The number of erosion iterations controls how aggressive the effect is.
 * Wraps the JH Labs ErodeFilter.
 */
public class ErodeFilter extends BufferedOpFilter {

    private final int iterations;
    private final int threshold;

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
        // jhlabs ErodeFilter default threshold is 2.
        this(iterations, 2);
    }

    /**
     * @param iterations the number of erosion passes; higher values erode more.
     * @param threshold  the number of neighbouring pixels required for erosion to occur.
     */
    public ErodeFilter(int iterations, int threshold) {
        this.iterations = iterations;
        this.threshold = threshold;
    }

    @Override
    public BufferedImageOp op() {
        thirdparty.jhlabs.image.ErodeFilter filter = new thirdparty.jhlabs.image.ErodeFilter();
        filter.setIterations(iterations);
        filter.setThreshold(threshold);
        return filter;
    }
}
