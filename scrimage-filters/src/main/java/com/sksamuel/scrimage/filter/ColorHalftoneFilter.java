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

public class ColorHalftoneFilter extends BufferedOpFilter {

    private final float radius;

    public ColorHalftoneFilter(float radius) {
        this.radius = radius;
    }

    public ColorHalftoneFilter() {
        this(1.2f);
    }

    @Override
    public BufferedImageOp op() {
        thirdparty.jhlabs.image.ColorHalftoneFilter op = new thirdparty.jhlabs.image.ColorHalftoneFilter();
        op.setdotRadius(radius);
        return op;
    }
}

