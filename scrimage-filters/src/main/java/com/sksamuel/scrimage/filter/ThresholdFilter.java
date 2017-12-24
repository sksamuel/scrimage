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

public class ThresholdFilter extends BufferedOpFilter {

    private final int threshold;
    private final int white;
    private final int black;

    public ThresholdFilter(int threshold, int white, int black) {
        this.threshold = threshold;
        this.white = white;
        this.black = black;
    }

    public ThresholdFilter(int threshold) {
        this(threshold, 0xffffff, 0x000000);
    }

    public ThresholdFilter() {
        this(127);
    }

    @Override
    public BufferedImageOp op() {
        thirdparty.jhlabs.image.ThresholdFilter op = new thirdparty.jhlabs.image.ThresholdFilter(threshold);
        op.setBlack(black);
        op.setWhite(white);
        return op;
    }
}