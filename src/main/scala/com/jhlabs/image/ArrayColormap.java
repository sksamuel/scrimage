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
 * A colormap implemented with an array of colors. This corresponds to the IndexColorModel class.
 */
public class ArrayColormap implements Colormap, Cloneable {
	
	/**
	 * The array of colors.
	 */
	protected int[] map;

	/**
	 * Construct an all-black colormap.
	 */
	public ArrayColormap() {
		this.map = new int[256];
	}

	/**
	 * Construct a colormap with the given map.
	 * @param map the array of ARGB colors
	 */
	public ArrayColormap(int[] map) {
		this.map = map;
	}

	public Object clone() {
		try {
			ArrayColormap g = (ArrayColormap)super.clone();
			g.map = (int[])map.clone();
			return g;
		}
		catch (CloneNotSupportedException e) {
		}
		return null;
	}
	
    /**
     * Set the array of colors for the colormap.
     * @param map the colors
     * @see #getMap
     */
	public void setMap(int[] map) {
		this.map = map;
	}

    /**
     * Get the array of colors for the colormap.
     * @return the colors
     * @see #setMap
     */
	public int[] getMap() {
		return map;
	}

	/**
	 * Convert a value in the range 0..1 to an RGB color.
	 * @param v a value in the range 0..1
	 * @return an RGB color
     * @see #setColor
	 */
	public int getColor(float v) {
/*
		v *= 255;
		int n = (int)v;
		float f = v-n;
		if (n < 0)
			return map[0];
		else if (n >= 255)
			return map[255];
		return ImageMath.mixColors(f, map[n], map[n+1]);
*/
		int n = (int)(v*255);
		if (n < 0)
			n = 0;
		else if (n > 255)
			n = 255;
		return map[n];
	}
	
	/**
	 * Set the color at "index" to "color". Entries are interpolated linearly from
	 * the existing entries at "firstIndex" and "lastIndex" to the new entry.
	 * firstIndex < index < lastIndex must hold.
     * @param index the position to set
     * @param firstIndex the position of the first color from which to interpolate
     * @param lastIndex the position of the second color from which to interpolate
     * @param color the color to set
	 */
	public void setColorInterpolated(int index, int firstIndex, int lastIndex, int color) {
		int firstColor = map[firstIndex];
		int lastColor = map[lastIndex];
		for (int i = firstIndex; i <= index; i++)
			map[i] = ImageMath.mixColors((float)(i-firstIndex)/(index-firstIndex), firstColor, color);
		for (int i = index; i < lastIndex; i++)
			map[i] = ImageMath.mixColors((float)(i-index)/(lastIndex-index), color, lastColor);
	}

    /**
     * Set a range of the colormap, interpolating between two colors.
     * @param firstIndex the position of the first color
     * @param lastIndex the position of the second color
     * @param color1 the first color
     * @param color2 the second color
     */
	public void setColorRange(int firstIndex, int lastIndex, int color1, int color2) {
		for (int i = firstIndex; i <= lastIndex; i++)
			map[i] = ImageMath.mixColors((float)(i-firstIndex)/(lastIndex-firstIndex), color1, color2);
	}

    /**
     * Set a range of the colormap to a single color.
     * @param firstIndex the position of the first color
     * @param lastIndex the position of the second color
     * @param color the color
     */
	public void setColorRange(int firstIndex, int lastIndex, int color) {
		for (int i = firstIndex; i <= lastIndex; i++)
			map[i] = color;
	}

    /**
     * Set one element of the colormap to a given color.
     * @param index the position of the color
     * @param color the color
     * @see #getColor
     */
	public void setColor(int index, int color) {
		map[index] = color;
	}

}
