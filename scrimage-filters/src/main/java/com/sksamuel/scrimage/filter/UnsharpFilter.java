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

import static com.sksamuel.scrimage.filter.EdgeAction.ClampEdges;

/**
 * Sharpens an image using the unsharp-mask technique. Wraps the JH Labs UnsharpFilter.
 */
public class UnsharpFilter extends BufferedOpFilter {

    private final float amount;
    private final int threshold;
    private final EdgeAction edgeAction;

    /**
     * @param amount     the amount of sharpening, 0 to 1 (jhlabs default 0.5)
     * @param threshold  the minimum difference for a pixel to be sharpened (jhlabs default 1)
     * @param edgeAction how the blur pass treats pixels beyond the image edge
     */
    public UnsharpFilter(float amount, int threshold, EdgeAction edgeAction) {
        this.amount = amount;
        this.threshold = threshold;
        this.edgeAction = edgeAction;
    }

    /**
     * Creates an UnsharpFilter with amount 0.5, threshold 1 and {@link EdgeAction#ClampEdges}.
     * <p>
     * Clamp is the edge handling this filter has always effectively used: the previous default
     * was declared as ZeroEdges, but the underlying op ignored the setting and clamped, so the
     * default output is unchanged.
     */
    public UnsharpFilter() {
        this(0.5f, 1, ClampEdges);
    }

    @Override
    public BufferedImageOp op() {
        thirdparty.jhlabs.image.UnsharpFilter op = new thirdparty.jhlabs.image.UnsharpFilter();
        op.setAmount(amount);
        op.setThreshold(threshold);
        switch (edgeAction) {
            case ZeroEdges:
                op.setEdgeAction(thirdparty.jhlabs.image.ConvolveFilter.ZERO_EDGES);
                break;
            case ClampEdges:
                op.setEdgeAction(thirdparty.jhlabs.image.ConvolveFilter.CLAMP_EDGES);
                break;
            case WrapEdges:
                op.setEdgeAction(thirdparty.jhlabs.image.ConvolveFilter.WRAP_EDGES);
                break;
        }
        return op;
    }
}
