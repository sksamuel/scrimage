package com.sksamuel.scrimage.filter;

import java.awt.image.BufferedImageOp;

/**
 * Adds a glint/sparkle to the bright parts of the image. Wraps the JH Labs
 * GlintFilter.
 *
 * <p>The intensity is controlled by the threshold (the brightness above which a
 * pixel glints, lower means more pixels glint), the amount (how strong the
 * glint is), the length of the star rays, and an optional blur applied before
 * thresholding.
 */
public class GlintFilter extends BufferedOpFilter {

    private final float threshold;
    private final float amount;
    private final int length;
    private final float blur;

    public GlintFilter(float threshold, float amount, int length, float blur) {
        this.threshold = threshold;
        this.amount = amount;
        this.length = length;
        this.blur = blur;
    }

    /** Uses the JH Labs defaults (threshold 1.0, amount 0.1, length 5, no blur). */
    public GlintFilter() {
        this(1.0f, 0.1f, 5, 0.0f);
    }

    @Override
    public BufferedImageOp op() {
        thirdparty.jhlabs.image.GlintFilter op = new thirdparty.jhlabs.image.GlintFilter();
        op.setThreshold(threshold);
        op.setAmount(amount);
        op.setLength(length);
        op.setBlur(blur);
        return op;
    }
}
