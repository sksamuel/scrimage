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

public class RippleFilter extends BufferedOpFilter {

    private final RippleType rippleType;
    private final float xAmplitude;
    private final float yAmplitude;
    private final float xWavelength;
    private final float yWavelength;

    public RippleFilter(RippleType rippleType, float xAmplitude, float yAmplitude, float xWavelength, float yWavelength) {
        this.rippleType = rippleType;
        this.xAmplitude = xAmplitude;
        this.yAmplitude = yAmplitude;
        this.xWavelength = xWavelength;
        this.yWavelength = yWavelength;
    }

    public RippleFilter(RippleType rippleType) {
        this(rippleType, 2f, 2f, 6f, 6f);
    }

    @Override
    public BufferedImageOp op() {
        thirdparty.jhlabs.image.RippleFilter op = new thirdparty.jhlabs.image.RippleFilter();
        op.setXAmplitude(xAmplitude);
        op.setYAmplitude(yAmplitude);
        op.setXWavelength(xWavelength);
        op.setYWavelength(yWavelength);
        switch (rippleType) {
            case Sine:
                op.setWaveType(thirdparty.jhlabs.image.RippleFilter.SINE);
                break;
            case Sawtooth:
                op.setWaveType(thirdparty.jhlabs.image.RippleFilter.SAWTOOTH);
                break;
            case Triangle:
                op.setWaveType(thirdparty.jhlabs.image.RippleFilter.TRIANGLE);
                break;
            case Noise:
                op.setWaveType(thirdparty.jhlabs.image.RippleFilter.NOISE);
                break;
        }
        return op;
    }
}
