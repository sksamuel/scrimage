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
 * A colormap which interpolates linearly between two colors.
 */
public class LinearColormap implements Colormap {

	private int color1;
	private int color2;

	/**
	 * Construct a color map with a grayscale ramp from black to white.
	 */
	public LinearColormap() {
		this(0xff000000, 0xffffffff);
	}

	/**
	 * Construct a linear color map.
	 * @param color1 the color corresponding to value 0 in the colormap
	 * @param color2 the color corresponding to value 1 in the colormap
	 */
	public LinearColormap(int color1, int color2) {
		this.color1 = color1;
		this.color2 = color2;
	}

	/**
	 * Set the first color.
	 * @param color1 the color corresponding to value 0 in the colormap
	 */
	public void setColor1(int color1) {
		this.color1 = color1;
	}

	/**
	 * Get the first color.
	 * @return the color corresponding to value 0 in the colormap
	 */
	public int getColor1() {
		return color1;
	}

	/**
	 * Set the second color.
	 * @param color2 the color corresponding to value 1 in the colormap
	 */
	public void setColor2(int color2) {
		this.color2 = color2;
	}

	/**
	 * Get the second color.
	 * @return the color corresponding to value 1 in the colormap
	 */
	public int getColor2() {
		return color2;
	}

	/**
	 * Convert a value in the range 0..1 to an RGB color.
	 * @param v a value in the range 0..1
	 * @return an RGB color
	 */
	public int getColor(float v) {
		return ImageMath.mixColors(ImageMath.clamp(v, 0, 1.0f), color1, color2);
	}
	
}
