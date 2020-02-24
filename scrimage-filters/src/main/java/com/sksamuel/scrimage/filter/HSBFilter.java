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

        assert hue <= 1;
        assert brightness <= 1;
        assert saturation <= 1;
        assert hue >= -1;
        assert brightness >= -1;
        assert saturation >= -1;

        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
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
