/*
 * Copyright 2013, Morten Nobel-Joergensen
 *
 * License: The BSD 3-Clause License
 * http://opensource.org/licenses/BSD-3-Clause
 */
package thirdparty.mortennobel;

/**
 * @author Heinz Doerr
 */
class BiCubicFilter implements ResampleFilter {

  protected final float a;

  public BiCubicFilter() {
    a = -0.5f;
  }

  protected BiCubicFilter(float a) {
    this.a = a;
  }

  public final float apply(float value) {
    if (value == 0)
      return 1.0f;
    if (value < 0.0f)
      value = -value;
    float vv = value * value;
    if (value < 1.0f) {
      return (a + 2f) * vv * value - (a + 3f) * vv + 1f;
    }
    if (value < 2.0f) {
      return a * vv * value - 5 * a * vv + 8 * a * value - 4 * a;
    }
    return 0.0f;
  }

  public float getSamplingRadius() {
    return 2.0f;
  }
}