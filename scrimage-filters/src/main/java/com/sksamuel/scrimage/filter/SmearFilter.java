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

public class SmearFilter extends BufferedOpFilter {

    private final SmearType smearType;
    private final float angle;
    private final float density;
    private final float scatter;
    private final int distance;
    private final float mix;
    private final int fadeout;

    public SmearFilter(SmearType smearType, float angle, float density, float scatter, int distance, float mix) {
        this(smearType, angle, density, scatter, distance, mix, 0);
    }

    /**
     * @param fadeout fades the smeared shapes out towards their edges (jhlabs default 0).
     */
    public SmearFilter(SmearType smearType, float angle, float density, float scatter, int distance, float mix, int fadeout) {
        if (smearType == null)
            throw new IllegalArgumentException("smearType must not be null");
        // The jhlabs implementation does `nextInt() % distance` in the
        // shape-drawing loops; distance == 0 throws ArithmeticException
        // ("/ by zero") deep inside filterPixels. Negative values give
        // negative `length` and degenerate output.
        if (distance < 1)
            throw new IllegalArgumentException("distance must be >= 1, got " + distance);
        if (density < 0 || density > 1)
            throw new IllegalArgumentException("density must be in [0, 1], got " + density);
        if (mix < 0 || mix > 1)
            throw new IllegalArgumentException("mix must be in [0, 1], got " + mix);
        this.smearType = smearType;
        this.angle = angle;
        this.density = density;
        this.scatter = scatter;
        this.distance = distance;
        this.mix = mix;
        this.fadeout = fadeout;
    }

    public SmearFilter(SmearType smearType) {
        this(smearType, 0, 0.3f, 0, 3, 0.4f);
    }

    @Override
    public BufferedImageOp op() {
        thirdparty.jhlabs.image.SmearFilter op = new thirdparty.jhlabs.image.SmearFilter();
        op.setDensity(density);
        op.setAngle(angle);
        op.setScatter(scatter);
        op.setDistance(distance);
        op.setMix(mix);
        op.setFadeout(fadeout);
        switch (smearType) {
            case Circles:
                op.setShape(thirdparty.jhlabs.image.SmearFilter.CIRCLES);
                break;
            case Crosses:
                op.setShape(thirdparty.jhlabs.image.SmearFilter.CROSSES);
                break;
            case Lines:
                op.setShape(thirdparty.jhlabs.image.SmearFilter.LINES);
                break;
            case Squares:
                op.setShape(thirdparty.jhlabs.image.SmearFilter.SQUARES);
                break;
        }
        return op;
    }
}
