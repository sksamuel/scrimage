/*
 * Copyright 2013, Morten Nobel-Joergensen
 *
 * License: The BSD 3-Clause License
 * http://opensource.org/licenses/BSD-3-Clause
 */
package thirdparty.mortennobel;

/**
 * A B-spline resample filter.
 */
public class BSplineFilter implements ResampleFilter {

    public float getSamplingRadius() {
        return 2.0f;
    }

    public final float apply(float value) {
        if (value < 0.0f) {
            value = -value;
        }
        if (value < 1.0f) {
            float tt = value * value;
            return 0.5f * tt * value - tt + (2.0f / 3.0f);
        } else if (value < 2.0f) {
            value = 2.0f - value;
            return (1.0f / 6.0f) * value * value * value;
        } else {
            return 0.0f;
        }
    }
}