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

/**
 * A grayscale colormap. Black is 0, white is 1.
 */
public class GrayscaleColormap implements Colormap  {
	
	public GrayscaleColormap() {
	}

	/**
	 * Convert a value in the range 0..1 to an RGB color.
	 * @param v a value in the range 0..1
	 * @return an RGB color
	 */
	public int getColor(float v) {
		int n = (int)(v*255);
		if (n < 0)
			n = 0;
		else if (n > 255)
			n = 255;
		return 0xff000000 | (n << 16) | (n << 8) | n;
	}
	
}
