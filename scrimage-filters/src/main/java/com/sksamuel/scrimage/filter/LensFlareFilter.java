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

import thirdparty.jhlabs.image.FlareFilter;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImageOp;

public class LensFlareFilter extends BufferedOpFilter {

    private final int color;
    private final float centreX;
    private final float centreY;

    public LensFlareFilter() {
        // jhlabs FlareFilter defaults: white flare, centre (0.5, 0.5).
        this(0xffffffff, 0.5f, 0.5f);
    }

    /**
     * @param color   the ARGB colour of the flare
     * @param centreX the centre of the flare in X, as a proportion of the image width [0, 1]
     * @param centreY the centre of the flare in Y, as a proportion of the image height [0, 1]
     */
    public LensFlareFilter(int color, float centreX, float centreY) {
        this.color = color;
        this.centreX = centreX;
        this.centreY = centreY;
    }

    @Override
    public BufferedImageOp op() {
        FlareFilter op = new FlareFilter();
        op.setRadius(70f);
        op.setRayAmount(2.2f);
        op.setRingWidth(3f);
        op.setRingAmount(0.2f);
        op.setBaseAmount(1.1f);
        op.setColor(color);
        op.setCentre(new Point2D.Float(centreX, centreY));
        return op;
    }
}

