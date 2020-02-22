/*
 * Copyright 2013, Morten Nobel-Joergensen
 *
 * License: The BSD 3-Clause License
 * http://opensource.org/licenses/BSD-3-Clause
 */
package thirdparty.mortennobel;

public final class Lanczos3Filter implements ResampleFilter {

  private final static float PI_FLOAT = (float) Math.PI;

  private float sincModified(float value) {
    return ((float) Math.sin(value)) / value;
  }

  public final float apply(float value) {
    if (value == 0) {
      return 1.0f;
    }
    if (value < 0.0f) {
      value = -value;
    }

    if (value < 3.0f) {
      value *= PI_FLOAT;
      return sincModified(value) * sincModified(value / 3.0f);
    } else {
      return 0.0f;
    }
  }

  public float getSamplingRadius() {
    return 3.0f;
  }
}