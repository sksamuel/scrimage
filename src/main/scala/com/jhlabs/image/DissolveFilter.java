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

import java.awt.image.*;
import java.util.*;

/**
 * A filter which "dissolves" an image by thresholding the alpha channel with random numbers.
 */
public class DissolveFilter extends PointFilter {
	
	private float density = 1;
	private float softness = 0;
	private float minDensity, maxDensity;
	private Random randomNumbers;
	
	public DissolveFilter() {
	}

	/**
	 * Set the density of the image in the range 0..1.
	 * @param density the density
     * @min-value 0
     * @max-value 1
     * @see #getDensity
	 */
	public void setDensity( float density ) {
		this.density = density;
	}
	
	/**
	 * Get the density of the image.
	 * @return the density
     * @see #setDensity
	 */
	public float getDensity() {
		return density;
	}
	
	/**
	 * Set the softness of the dissolve in the range 0..1.
	 * @param softness the softness
     * @min-value 0
     * @max-value 1
     * @see #getSoftness
	 */
	public void setSoftness( float softness ) {
		this.softness = softness;
	}
	
	/**
	 * Get the softness of the dissolve.
	 * @return the softness
     * @see #setSoftness
	 */
	public float getSoftness() {
		return softness;
	}
	
    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
		float d = (1-density) * (1+softness);
		minDensity = d-softness;
		maxDensity = d;
		randomNumbers = new Random( 0 );
		return super.filter( src, dst );
	}
	
	public int filterRGB(int x, int y, int rgb) {
		int a = (rgb >> 24) & 0xff;
		float v = randomNumbers.nextFloat();
		float f = ImageMath.smoothStep( minDensity, maxDensity, v );
		return ((int)(a * f) << 24) | rgb & 0x00ffffff;
	}

	public String toString() {
		return "Stylize/Dissolve...";
	}
}
