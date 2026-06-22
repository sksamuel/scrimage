package com.sksamuel.scrimage.filter;

import java.awt.image.BufferedImageOp;

public class QuantizeFilter extends BufferedOpFilter {

    private final int colors;
    private final boolean dither;
    private final boolean serpentine;

    /**
     * @param colors     the number of colors to quantize to. The default is 256.
     * @param dither     true to use dithering. If not, the image is posterized.
     * @param serpentine true to use a serpentine pattern for return. This can reduce 'avalanche' artifacts in the output.
     */
    public QuantizeFilter(int colors, boolean dither, boolean serpentine) {
        this.colors = colors;
        this.dither = dither;
        this.serpentine = serpentine;
    }

    /**
     * @param colors the number of colors to quantize to. The default is 256.
     * @param dither true to use dithering. If not, the image is posterized.
     */
    public QuantizeFilter(int colors, boolean dither) {
        this(colors, dither, true);
    }

    /**
     * @param colors the number of colors to quantize to. The default is 256.
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
