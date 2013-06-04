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
 * A filter which performs one round of the game of Life on an image.
 */
public class LifeFilter extends BinaryFilter {

	public LifeFilter() {
	}

	protected int[] filterPixels( int width, int height, int[] inPixels, Rectangle transformedSpace ) {
		int index = 0;
		int[] outPixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int r = 0, g = 0, b = 0;
				int pixel = inPixels[y*width+x];
				int a = pixel & 0xff000000;
				int neighbours = 0;

				for (int row = -1; row <= 1; row++) {
					int iy = y+row;
					int ioffset;
					if (0 <= iy && iy < height) {
						ioffset = iy*width;
						for (int col = -1; col <= 1; col++) {
							int ix = x+col;
							if (!(row == 0 && col == 0) && 0 <= ix && ix < width) {
								int rgb = inPixels[ioffset+ix];
								if (blackFunction.isBlack(rgb))
									neighbours++;
							}
						}
					}
				}
				
				if (blackFunction.isBlack(pixel))
					outPixels[index++] = (neighbours == 2 || neighbours == 3) ? pixel : 0xffffffff;
				else
					outPixels[index++] = neighbours == 3 ? 0xff000000 : pixel;
			}

		}
		return outPixels;
	}

	public String toString() {
		return "Binary/Life";
	}

}

