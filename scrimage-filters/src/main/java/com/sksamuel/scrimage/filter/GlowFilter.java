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

public class GlowFilter extends BufferedOpFilter {

    private final float amount;

    public GlowFilter(float amount) {
        this.amount = amount;
    }

    public GlowFilter() {
        this(0.5f);
    }

    @Override
    public BufferedImageOp op() {
        thirdparty.jhlabs.image.GlowFilter op = new thirdparty.jhlabs.image.GlowFilter();
        op.setAmount(amount);
        return op;
    }
}