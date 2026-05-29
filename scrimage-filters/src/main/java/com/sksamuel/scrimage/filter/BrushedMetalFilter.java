package com.sksamuel.scrimage.filter;

import java.awt.image.BufferedImageOp;

/**
 * Produces a brushed-metal texture from the image. Wraps the JH Labs
 * BrushedMetalFilter.
 */
public class BrushedMetalFilter extends BufferedOpFilter {

    @Override
    public BufferedImageOp op() {
        return new thirdparty.jhlabs.image.BrushedMetalFilter();
    }
}
