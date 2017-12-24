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

public class SparkleFilter extends BufferedOpFilter {

    private final int x;
    private final int y;
    private final int rays;
    private final int radius;
    private final int amount;

    public SparkleFilter(int x, int y, int rays, int radius, int amount) {
        this.x = x;
        this.y = y;
        this.rays = rays;
        this.radius = radius;
        this.amount = amount;
    }

    public SparkleFilter() {
        this(0, 0, 50, 25, 50);
    }

    @Override
    public BufferedImageOp op() {
        thirdparty.jhlabs.image.SparkleFilter op = new thirdparty.jhlabs.image.SparkleFilter();
        op.setRays(rays);
        op.setRadius(radius);
        op.setAmount(amount);
        op.setDimensions(x, y);
        return op;
    }
}