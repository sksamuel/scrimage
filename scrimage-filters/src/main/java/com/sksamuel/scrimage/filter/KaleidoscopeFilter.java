/*
   Copyright 2014 Stephen K Samuel

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

public class KaleidoscopeFilter extends BufferedOpFilter {

    private final int sides;
    private final float angle;
    private final float centreX;
    private final float centreY;
    private final float radius;

    public KaleidoscopeFilter() {
        this(3);
    }

    public KaleidoscopeFilter(int sides) {
        // jhlabs defaults: angle 0, centre (0.5, 0.5), radius 0.
        this(sides, 0f, 0.5f, 0.5f, 0f);
    }

    /**
     * @param sides   the number of reflected segments (must be &gt; 2)
     * @param angle   the rotation angle of the kaleidoscope, in radians
     * @param centreX the centre of the effect in X, as a proportion of the image width [0, 1]
     * @param centreY the centre of the effect in Y, as a proportion of the image height [0, 1]
     * @param radius  the radius of the effect in pixels (0 applies it across the whole image)
     */
    public KaleidoscopeFilter(int sides, float angle, float centreX, float centreY, float radius) {
        if (sides < 3)
            throw new IllegalArgumentException("'sides' must be greater than 2");
        this.sides = sides;
        this.angle = angle;
        this.centreX = centreX;
        this.centreY = centreY;
        this.radius = radius;
    }

    @Override
    public BufferedImageOp op() {
        thirdparty.jhlabs.image.KaleidoscopeFilter filter = new thirdparty.jhlabs.image.KaleidoscopeFilter();
        filter.setSides(sides);
        filter.setAngle(angle);
        filter.setCentreX(centreX);
        filter.setCentreY(centreY);
        filter.setRadius(radius);
        return filter;
    }
}
