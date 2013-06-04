/*
Copyright 2006 Jerry Huxtable

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

package com.jhlabs.image;

import java.awt.*;
import java.awt.image.*;

/**
 * A filter which fills an image with a given color. Normally you would just call Graphics.fillRect but it can sometimes be useful
 * to go via a filter to fit in with an existing API.
 */
public class FillFilter extends PointFilter {

	private int fillColor;

    /**
     * Construct a FillFilter.
     */
	public FillFilter() {
		this(0xff000000);
	}

    /**
     * Construct a FillFilter.
     * @param color the fill color
     */
	public FillFilter(int color) {
		this.fillColor = color;
	}

    /**
     * Set the fill color.
     * @param fillColor the fill color
     * @see #getFillColor
     */
	public void setFillColor(int fillColor) {
		this.fillColor = fillColor;
	}

    /**
     * Get the fill color.
     * @return the fill color
     * @see #setFillColor
     */
	public int getFillColor() {
		return fillColor;
	}

	public int filterRGB(int x, int y, int rgb) {
		return fillColor;
	}
}

