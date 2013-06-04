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
 * A filter to change the saturation of an image. This works by calculating a grayscale version of the image
 * and then extrapolating away from it.
 */
public class SaturationFilter extends PointFilter {
	
	public float amount = 1;
	
    /**
     * Construct a SaturationFilter.
     */
	public SaturationFilter() {
	}

    /**
     * Construct a SaturationFilter.
     * The amount of saturation change.
     */
	public SaturationFilter( float amount ) {
		this.amount = amount;
		canFilterIndexColorModel = true;
	}

    /**
     * Set the amount of saturation change. 1 leaves the image unchanged, values between 0 and 1 desaturate, 0 completely
     * desaturates it and values above 1 increase the saturation.
     * @param amount the amount
     */
	public void setAmount( float amount ) {
		this.amount = amount;
	}
	
    /**
     * Set the amount of saturation change.
     * @return the amount
     */
	public float getAmount() {
		return amount;
	}
	
	public int filterRGB(int x, int y, int rgb) {
		if ( amount != 1 ) {
            int a = rgb & 0xff000000;
            int r = (rgb >> 16) & 0xff;
            int g = (rgb >> 8) & 0xff;
            int b = rgb & 0xff;
            int v = ( r + g + b )/3; // or a better brightness calculation if you prefer
            r = PixelUtils.clamp( (int)(v + amount * (r-v)) );
            g = PixelUtils.clamp( (int)(v + amount * (g-v)) );
            b = PixelUtils.clamp( (int)(v + amount * (b-v)) );
            return a | (r << 16) | (g << 8) | b;
        }
        return rgb;
	}

	public String toString() {
		return "Colors/Saturation...";
	}
}

