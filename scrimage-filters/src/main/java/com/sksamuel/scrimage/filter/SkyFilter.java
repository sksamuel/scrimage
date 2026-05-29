package com.sksamuel.scrimage.filter;

import java.awt.image.BufferedImageOp;

/**
 * Generates a cloudy sky effect. Wraps the JH Labs SkyFilter.
 */
public class SkyFilter extends BufferedOpFilter {

    @Override
    public BufferedImageOp op() {
        return new thirdparty.jhlabs.image.SkyFilter();
    }
}
