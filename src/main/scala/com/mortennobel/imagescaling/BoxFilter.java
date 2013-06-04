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
 * A box filter (also known as nearest neighbor).
 */
final class BoxFilter implements ResampleFilter
{
	public float getSamplingRadius() {
		return 0.5f;
	}

	public final float apply(float value)
	{
		if (value > -0.5f && value <= 0.5f)
		{
			return 1.0f;
		}
		else
		{
			return 0.0f;
		}
	}

	public String getName() {
		return "Box";
	}
}
