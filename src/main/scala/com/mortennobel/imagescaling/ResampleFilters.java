/*
 * Copyright 2009, Morten Nobel-Joergensen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.mortennobel.imagescaling;

public class ResampleFilters {
    private static BiCubicFilter biCubicFilter = new BiCubicFilter();
    private static BSplineFilter bSplineFilter = new BSplineFilter();
    private static Lanczos3Filter lanczos3Filter = new Lanczos3Filter();
    private static TriangleFilter triangleFilter = new TriangleFilter();


    public static ResampleFilter getBiCubicFilter() {
        return biCubicFilter;
    }


    public static ResampleFilter getBSplineFilter() {
        return bSplineFilter;
    }


    public static ResampleFilter getLanczos3Filter() {
        return lanczos3Filter;
    }


    public static ResampleFilter getTriangleFilter() {
        return triangleFilter;
    }
}
