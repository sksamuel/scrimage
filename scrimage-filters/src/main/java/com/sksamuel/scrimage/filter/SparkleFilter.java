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

public class SparkleFilter extends BufferedOpFilter {

    private final int x;
    private final int y;
    private final int rays;
    private final int radius;
    private final int amount;

    public SparkleFilter(int x, int y, int rays, int radius, int amount) {
        // jhlabs SparkleFilter indexes rayLengths[i % rays] for every pixel,
        // so rays == 0 throws ArithmeticException (/ by zero) and rays < 0
        // allocates a negative-length array. Reject up front, mirroring the
        // input validation in the SmearFilter and OilFilter wrappers.
        if (rays < 1)
            throw new IllegalArgumentException("rays must be >= 1, got " + rays);
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
        // Capture the configured sparkle centre into locals so the anonymous
        // subclass can re-apply them after jhlabs' setDimensions clobbers
        // centreX/centreY back to (width/2, height/2). PointFilter.filter
        // calls setDimensions(srcWidth, srcHeight) right before iterating,
        // so without this override the constructor's (x, y) is dead.
        //
        // (0, 0) is kept as a sentinel meaning "image centre" so the
        // pre-existing no-arg SparkleFilter() still produces a centred
        // sparkle. Any other (x, y) is honoured as a pixel coordinate.
        final int cx = x;
        final int cy = y;
        thirdparty.jhlabs.image.SparkleFilter op = new thirdparty.jhlabs.image.SparkleFilter() {
            @Override
            public void setDimensions(int width, int height) {
                super.setDimensions(width, height);
                if (cx != 0 || cy != 0) {
                    setCentreX(cx);
                    setCentreY(cy);
                }
            }
        };
        op.setRays(rays);
        op.setRadius(radius);
        op.setAmount(amount);
        return op;
    }
}
