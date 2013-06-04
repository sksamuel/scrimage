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
 * The Mitchell resample filter.
 */
final class MitchellFilter implements ResampleFilter
{
	private static final float B = 1.0f / 3.0f;
	private static final float C = 1.0f / 3.0f;

	public float getSamplingRadius() {
		return 2.0f;
	}

	public final float apply(float value)
	{
		if (value < 0.0f)
		{
			value = -value;
		}
		float tt = value * value;
		if (value < 1.0f)
		{
			value = (((12.0f - 9.0f * B - 6.0f * C) * (value * tt))
			+ ((-18.0f + 12.0f * B + 6.0f * C) * tt)
			+ (6.0f - 2f * B));
			return value / 6.0f;
		}
		else
		if (value < 2.0f)
		{
			value = (((-1.0f * B - 6.0f * C) * (value * tt))
			+ ((6.0f * B + 30.0f * C) * tt)
			+ ((-12.0f * B - 48.0f * C) * value)
			+ (8.0f * B + 24 * C));
			return value / 6.0f;
		}
		else
		{
			return 0.0f;
		}
	}

	public String getName() {
		return "BSpline";
	}
}

