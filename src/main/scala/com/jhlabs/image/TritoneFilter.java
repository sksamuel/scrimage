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
 * A filter which performs a tritone conversion on an image. Given three colors for shadows, midtones and highlights,
 * it converts the image to grayscale and then applies a color mapping based on the colors.
 */
public class TritoneFilter extends PointFilter {

	private int shadowColor = 0xff000000;
	private int midColor = 0xff888888;
	private int highColor = 0xffffffff;
    private int[] lut;
	
    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        lut = new int[256];
        for ( int i = 0; i < 128; i++ ) {
            float t = i / 127.0f;
            lut[i] = ImageMath.mixColors( t, shadowColor, midColor );
        }
        for ( int i = 128; i < 256; i++ ) {
            float t = (i-127) / 128.0f;
            lut[i] = ImageMath.mixColors( t, midColor, highColor );
        }
        dst = super.filter( src, dst );
        lut = null;
        return dst;
    }
    
	public int filterRGB( int x, int y, int rgb ) {
        return lut[ PixelUtils.brightness( rgb ) ];
	}

    /**
     * Set the shadow color.
     * @param shadowColor the shadow color
     * @see #getShadowColor
     */
	public void setShadowColor( int shadowColor ) {
		this.shadowColor = shadowColor;
	}
	
    /**
     * Get the shadow color.
     * @return the shadow color
     * @see #setShadowColor
     */
	public int getShadowColor() {
		return shadowColor;
	}

    /**
     * Set the mid color.
     * @param midColor the mid color
     * @see #getmidColor
     */
	public void setMidColor( int midColor ) {
		this.midColor = midColor;
	}
	
    /**
     * Get the mid color.
     * @return the mid color
     * @see #setmidColor
     */
	public int getMidColor() {
		return midColor;
	}

    /**
     * Set the high color.
     * @param highColor the high color
     * @see #gethighColor
     */
	public void setHighColor( int highColor ) {
		this.highColor = highColor;
	}
	
    /**
     * Get the high color.
     * @return the high color
     * @see #sethighColor
     */
	public int getHighColor() {
		return highColor;
	}


	public String toString() {
		return "Colors/Tritone...";
	}

}

