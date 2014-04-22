/*
 * Copyright 2013, Morten Nobel-Joergensen
 *
 * License: The BSD 3-Clause License
 * http://opensource.org/licenses/BSD-3-Clause
 */
package thirdparty.mortennobel;

public class ResampleFilters {
  public static BiCubicFilter biCubicFilter = new BiCubicFilter();
  public static BSplineFilter bSplineFilter = new BSplineFilter();
  public static Lanczos3Filter lanczos3Filter = new Lanczos3Filter();
  public static TriangleFilter triangleFilter = new TriangleFilter();
}