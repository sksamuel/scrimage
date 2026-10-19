package com.sksamuel.scrimage.filter;

import java.awt.image.BufferedImageOp;

/**
 * Reduces the image to a limited palette using an octree quantizer, optionally
 * with error-diffusion dithering. Wraps the JH Labs QuantizeFilter.
 * <p>
 * Note: the underlying quantizer clamps the requested colour count to the range
 * [8, 256]. Values below 8 behave as 8 (e.g. {@code QuantizeFilter(2)} yields up to
 * 8 colours, not 2) and values above 256 behave as 256. Only a count below 1 is
 * rejected.
 */
public class QuantizeFilter extends BufferedOpFilter {

    private final int colors;
    private final boolean dither;
    private final boolean serpentine;

    /**
     * @param colors     the number of colors to quantize to. The default is 256. Clamped to [8, 256]; see the class javadoc.
     * @param dither     true to use dithering. If not, the image is posterized.
     * @param serpentine true to use a serpentine pattern for return. This can reduce 'avalanche' artifacts in the output.
     * @throws IllegalArgumentException if colors is less than 1
     */
    public QuantizeFilter(int colors, boolean dither, boolean serpentine) {
        if (colors < 1)
            throw new IllegalArgumentException("colors must be >= 1, got " + colors);
        this.colors = colors;
        this.dither = dither;
        this.serpentine = serpentine;
    }

    /**
     * @param colors the number of colors to quantize to. The default is 256. Clamped to [8, 256]; see the class javadoc.
     * @param dither true to use dithering. If not, the image is posterized.
     */
    public QuantizeFilter(int colors, boolean dither) {
        this(colors, dither, true);
    }

    /**
     * @param colors the number of colors to quantize to. The default is 256. Clamped to [8, 256]; see the class javadoc.
     */
    public QuantizeFilter(int colors) {
        this(colors, false);
    }

    public QuantizeFilter() {
        this(256, false);
    }

    @Override
    public BufferedImageOp op() {
        thirdparty.jhlabs.image.QuantizeFilter op = new thirdparty.jhlabs.image.QuantizeFilter();
        op.setNumColors(colors);
        op.setDither(dither);
        op.setSerpentine(serpentine);
        return op;
    }
}
