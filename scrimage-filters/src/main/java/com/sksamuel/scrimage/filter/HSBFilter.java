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

import thirdparty.jhlabs.image.HSBAdjustFilter;

import java.awt.image.BufferedImageOp;

public class HSBFilter extends BufferedOpFilter {

    private final float hue;
    private final float saturation;
    private final float brightness;

    public HSBFilter(float hue, float saturation, float brightness) {
        // Plain `assert` is a no-op without -ea, so the documented
        // [-1, 1] contract wasn't enforced in production — a hue of
        // 10 silently produced visually wrong output rather than
        // failing fast. Use IllegalArgumentException for the same
        // explicit validation other filters in this package use.
        requireUnitFactor("hue", hue);
        requireUnitFactor("saturation", saturation);
        requireUnitFactor("brightness", brightness);

        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
    }

    private static void requireUnitFactor(String name, float value) {
        if (!(value >= -1f && value <= 1f))
            throw new IllegalArgumentException(name + " must be in [-1, 1], got " + value);
    }

    public HSBFilter() {
        this(0, 0, 0);
    }

    @Override
    public BufferedImageOp op() {
        HSBAdjustFilter op = new HSBAdjustFilter();
        op.setHFactor(hue);
        op.setSFactor(saturation);
        op.setBFactor(brightness);
        return op;
    }

}
