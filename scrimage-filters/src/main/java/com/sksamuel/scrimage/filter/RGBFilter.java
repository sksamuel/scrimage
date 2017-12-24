package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.BufferedOpFilter;
import thirdparty.jhlabs.image.RGBAdjustFilter;

import java.awt.image.BufferedImageOp;

public class RGBFilter extends BufferedOpFilter {
    //  require(r <= 1)
//  require(g <= 1)
//  require(b <= 1)

    private final float r;
    private final float g;
    private final float b;

    public RGBFilter(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public RGBFilter() {
        this(0, 0, 0);
    }

    @Override
    public BufferedImageOp op() {
        RGBAdjustFilter op = new RGBAdjustFilter();
        op.setBFactor(b);
        op.setRFactor(r);
        op.setGFactor(g);
        return op;
    }

}
