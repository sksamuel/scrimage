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
import thirdparty.jhlabs.image.GainFilter;

import java.awt.image.BufferedImageOp;

public class GainBiasFilter extends BufferedOpFilter {

    private final float gain;
    private final float bias;

    public GainBiasFilter(float gain, float bias) {
        this.gain = gain;
        this.bias = bias;
    }

    public GainBiasFilter() {
        this(0.5f, 0.5f);
    }

    @Override
    public BufferedImageOp op() {
        GainFilter op = new GainFilter();
        op.setGain(gain);
        op.setBias(bias);
        return op;
    }
}
