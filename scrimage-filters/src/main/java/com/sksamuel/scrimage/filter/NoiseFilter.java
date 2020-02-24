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

public class NoiseFilter extends BufferedOpFilter {

    private final int amount;
    private final double density;

    public NoiseFilter(int amount, double density) {
        this.amount = amount;
        this.density = density;
    }

    public NoiseFilter() {
        this(25, 1);
    }

    @Override
    public BufferedImageOp op() {
        thirdparty.jhlabs.image.NoiseFilter op = new thirdparty.jhlabs.image.NoiseFilter();
        op.setDensity((float) density);
        op.setAmount(amount);
        return op;
    }
}

