package com.sksamuel.scrimage.filter;

import java.awt.image.BufferedImageOp;

/**
 * Renders the image as if woven into a basket-weave pattern.
 * Wraps the JH Labs WeaveFilter.
 */
public class WeaveFilter extends BufferedOpFilter {

    @Override
    public BufferedImageOp op() {
        return new thirdparty.jhlabs.image.WeaveFilter();
    }
}
