package com.sksamuel.scrimage.filter;

import java.awt.image.BufferedImageOp;

/**
 * Reduces a binary image to a skeleton, using the Zhang-Suen algorithm.
 * Wraps the JH Labs SkeletonFilter.
 */
public class SkeletonFilter extends BufferedOpFilter {

    @Override
    public BufferedImageOp op() {
        return new thirdparty.jhlabs.image.SkeletonFilter();
    }
}
