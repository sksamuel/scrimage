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
 * A filter to perform auto-equalization on an image.
 */
public class EqualizeFilter extends WholeImageFilter {

    private int[][] lut;

	public EqualizeFilter() {
	}

	protected int[] filterPixels( int width, int height, int[] inPixels, Rectangle transformedSpace ) {
		Histogram histogram = new Histogram(inPixels, width, height, 0, width);

		int i, j;

		if (histogram.getNumSamples() > 0) {
			float scale = 255.0f / histogram.getNumSamples();
			lut = new int[3][256];
			for (i = 0; i < 3; i++) {
				lut[i][0] = histogram.getFrequency(i, 0);
				for (j = 1; j < 256; j++)
					lut[i][j] = lut[i][j-1] + histogram.getFrequency(i, j);
				for (j = 0; j < 256; j++)
					lut[i][j] = (int)Math.round(lut[i][j]*scale);
			}
		} else
			lut = null;

		i = 0;
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) {
				inPixels[i] = filterRGB(x, y, inPixels[i]);
				i++;
			}
		lut = null;
		
		return inPixels;
	}

	private int filterRGB(int x, int y, int rgb) {
		if (lut != null) {
			int a = rgb & 0xff000000;
			int r = lut[Histogram.RED][(rgb >> 16) & 0xff];
			int g = lut[Histogram.GREEN][(rgb >> 8) & 0xff];
			int b = lut[Histogram.BLUE][rgb & 0xff];

			return a | (r << 16) | (g << 8) | b;
		}
		return rgb;
	}

	public String toString() {
		return "Colors/Equalize";
	}
}
