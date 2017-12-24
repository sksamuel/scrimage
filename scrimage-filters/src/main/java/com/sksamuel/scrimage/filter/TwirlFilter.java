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

public class TwirlFilter extends BufferedOpFilter {

    private final float angle;
    private final float radius;
    private final float centerX;
    private final float centerY;

    public TwirlFilter(float angle, float radius, float centerX, float centerY) {
        this.angle = angle;
        this.radius = radius;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public TwirlFilter(float angle, float radius) {
        this(angle, radius, 0.5f, 0.5f);
    }

    public TwirlFilter(float radius) {
        this((float) (Math.PI / 1.5), radius, 0.5f, 0.5f);
    }

    @Override
    public BufferedImageOp op() {
        thirdparty.jhlabs.image.TwirlFilter op = new thirdparty.jhlabs.image.TwirlFilter();
        op.setCentreX(centerX);
        op.setCentreY(centerY);
        op.setRadius(radius);
        op.setAngle(angle);
        return op;
    }
}
