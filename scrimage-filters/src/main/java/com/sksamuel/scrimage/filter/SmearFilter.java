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

import com.sksamuel.scrimage.BufferedOpFilter;

import java.awt.image.BufferedImageOp;

public class SmearFilter extends BufferedOpFilter {

    private final SmearType smearType;
    private final float angle;
    private final float density;
    private final float scatter;
    private final int distance;
    private final float mix;

    public SmearFilter(SmearType smearType, float angle, float density, float scatter, int distance, float mix) {
        this.smearType = smearType;
        this.angle = angle;
        this.density = density;
        this.scatter = scatter;
        this.distance = distance;
        this.mix = mix;
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