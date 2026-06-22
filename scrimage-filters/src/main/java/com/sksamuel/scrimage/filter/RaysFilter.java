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

public class RaysFilter extends BufferedOpFilter {

    private final float opacity;
    private final float threshold;
    private final float strength;
    private final float angle;
    private final float distance;
    private final float zoom;
    private final float rotation;
    private final boolean raysOnly;

    public RaysFilter(float opacity, float threshold, float strength,
                      float angle, float distance, float zoom, float rotation) {
        this(opacity, threshold, strength, angle, distance, zoom, rotation, false);
    }

    /**
     * @param raysOnly when true, only the rays are rendered (over a black background) rather
     *                 than composited over the source image.
     */
    public RaysFilter(float opacity, float threshold, float strength,
                      float angle, float distance, float zoom, float rotation, boolean raysOnly) {
        this.opacity = opacity;
        this.threshold = threshold;
        this.strength = strength;
        this.angle = angle;
        this.distance = distance;
        this.zoom = zoom;
        this.rotation = rotation;
        this.raysOnly = raysOnly;
    }

    public RaysFilter(float opacity, float threshold, float strength) {
        // Pick non-zero motion-blur defaults so the filter actually
        // produces rays. jhlabs RaysFilter extends MotionBlurOp; with
        // all four motion knobs (angle/distance/zoom/rotation) at their
        // zero defaults MotionBlurOp.filter() short-circuits with
        // maxDistance == 0 and returns a copy of the thresholded
        // luminance image with no radial component — the user sees a
        // faint grey overlay, never rays.
        this(opacity, threshold, strength, 0f, 0f, 0.35f, 0f);
    }

    public RaysFilter() {
        this(1.0f, 0, 0.5f);
    }

    @Override
    public BufferedImageOp op() {
        thirdparty.jhlabs.image.RaysFilter op = new thirdparty.jhlabs.image.RaysFilter();
        op.setOpacity(opacity);
        op.setThreshold(threshold);
        op.setStrength(strength);
        op.setAngle(angle);
        op.setDistance(distance);
        op.setZoom(zoom);
        op.setRotation(rotation);
        op.setRaysOnly(raysOnly);
        return op;
    }
}
