package com.sksamuel.scrimage.filter;

import java.awt.image.BufferedImageOp;

/**
 * Adds a glint/sparkle to the bright parts of the image. Wraps the JH Labs
 * GlintFilter.
 */
public class GlintFilter extends BufferedOpFilter {

    @Override
    public BufferedImageOp op() {
        return new thirdparty.jhlabs.image.GlintFilter();
    }
}
