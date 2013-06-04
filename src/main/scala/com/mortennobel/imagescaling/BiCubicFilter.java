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
 */
package com.mortennobel.imagescaling;


/**
 * @author Heinz Doerr
 */
class BiCubicFilter implements ResampleFilter {

	final protected float a;

	public BiCubicFilter() {
		a= -0.5f;
	}

	protected BiCubicFilter(float a) {
		this.a= a;
	}

	public final float apply(float value) {
		if (value == 0)
			return 1.0f;
		if (value < 0.0f)
			value = -value;
		float vv= value * value;
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

    public String getName()
	{
		return "BiCubic"; // also called cardinal cubic spline
	}
}
