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
 * This filter tries to apply the Swing "flush 3D" effect to the black lines in an image.
 */
public class Flush3DFilter extends WholeImageFilter {

	public Flush3DFilter() {
	}

	protected int[] filterPixels( int width, int height, int[] inPixels, Rectangle transformedSpace ) {
		int index = 0;
		int[] outPixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = inPixels[y*width+x];

				if (pixel != 0xff000000 && y > 0 && x > 0) {
					int count = 0;
					if (inPixels[y*width+x-1] == 0xff000000)
						count++;
					if (inPixels[(y-1)*width+x] == 0xff000000)
						count++;
					if (inPixels[(y-1)*width+x-1] == 0xff000000)
						count++;
					if (count >= 2)
						pixel = 0xffffffff;
				}
				outPixels[index++] = pixel;
			}

		}
		return outPixels;
	}

	public String toString() {
		return "Stylize/Flush 3D...";
	}

}

