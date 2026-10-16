/*
   Copyright 2013 Stephen K Samuel

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.sksamuel.scrimage.filter;

import java.awt.image.BufferedImageOp;

/**
 * Applies gamma correction to the red, green and blue channels. A gamma of 1.0
 * leaves the image unchanged; values above 1.0 brighten the midtones and values
 * below 1.0 darken them. Wraps the JH Labs GammaFilter.
 */
public class GammaFilter extends BufferedOpFilter {

    private final double gamma;

    /**
     * @param gamma the gamma level to apply to all RGB channels; must be a positive number.
     * @throws IllegalArgumentException if gamma is NaN or not greater than zero
     */
    public GammaFilter(double gamma) {
        // The jhlabs implementation builds its lookup table with pow(x, 1 / gamma):
        // gamma 0 gives an infinite exponent and an all-black table, and negative
        // or NaN gamma silently produces a black or garbage table too.
        if (Double.isNaN(gamma) || gamma <= 0)
            throw new IllegalArgumentException("gamma must be a positive number, got " + gamma);
        this.gamma = gamma;
    }

    public GammaFilter() {
        this(1.0f);
    }

    @Override
    public BufferedImageOp op() {
        thirdparty.jhlabs.image.GammaFilter op = new thirdparty.jhlabs.image.GammaFilter();
        op.setGamma((float) gamma);
        return op;
    }
}
