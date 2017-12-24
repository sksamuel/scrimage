package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.BufferedOpFilter;

import java.awt.image.BufferedImageOp;

public class QuantizeFilter extends BufferedOpFilter {

    private final int colors;
    private final boolean dither;

    public QuantizeFilter(int colors, boolean dither) {
        this.colors = colors;
        this.dither = dither;
    }

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
        return op;
    }
}

