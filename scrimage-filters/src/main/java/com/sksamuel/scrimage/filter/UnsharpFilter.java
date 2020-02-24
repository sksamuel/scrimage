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

import static com.sksamuel.scrimage.filter.EdgeAction.ZeroEdges;

public class UnsharpFilter extends BufferedOpFilter {

    private final float amount;
    private final int threshold;
    private final EdgeAction edgeAction;

    public UnsharpFilter(float amount, int threshold, EdgeAction edgeAction) {
        this.amount = amount;
        this.threshold = threshold;
        this.edgeAction = edgeAction;
    }

    public UnsharpFilter() {
        this(0.5f, 1, ZeroEdges);
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
