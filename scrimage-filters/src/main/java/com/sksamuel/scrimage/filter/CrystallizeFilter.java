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

public class CrystallizeFilter extends BufferedOpFilter {

    private final double scale;
    private final double edgeThickness;
    private final int edgeColor;
    private final double randomness;

    public CrystallizeFilter(double scale, double edgeThickness, int edgeColor, double randomness) {
        this.scale = scale;
        this.edgeThickness = edgeThickness;
        this.edgeColor = edgeColor;
        this.randomness = randomness;
    }

    public CrystallizeFilter() {
        this(16, 0.4, 0xff000000, 0.2);
    }

    @Override
    public BufferedImageOp op() {
        thirdparty.jhlabs.image.CrystallizeFilter op = new thirdparty.jhlabs.image.CrystallizeFilter();
        op.setEdgeColor(edgeColor);
        op.setEdgeThickness((float) edgeThickness);
        op.setScale((float) scale);
        op.setRandomness((float) randomness);
        return op;
    }
}
