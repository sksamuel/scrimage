package com.sksamuel.scrimage.filter;

import java.awt.image.BufferedImageOp;

public class ShearFilter extends BufferedOpFilter {

    private final float xAngle;
    private final float yAngle;

    public ShearFilter(float xAngle, float yAngle) {
        this.xAngle = xAngle;
        this.yAngle = yAngle;
    }

    @Override
    public BufferedImageOp op() {
        thirdparty.jhlabs.image.ShearFilter op = new thirdparty.jhlabs.image.ShearFilter();
        op.setXAngle(xAngle);
        op.setYAngle(yAngle);
        op.setResize(false);
        return op;
    }
}
