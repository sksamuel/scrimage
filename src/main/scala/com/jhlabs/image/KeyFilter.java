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
import java.util.*;

/**
 * An experimental filter which can be used for keying against a clean shot. Given a source image, a clean image and a destination image, 
 * the filter replaces all pixels in the source which nearly equal the equivalent clean pixel by destination pixels.
 */
public class KeyFilter extends AbstractBufferedImageOp {
	
	private float hTolerance = 0;
	private float sTolerance = 0;
	private float bTolerance = 0;
	private BufferedImage destination;
	private BufferedImage cleanImage;

	public KeyFilter() {
	}

	/**
	 * Set the hue tolerance of the image in the range 0..1.
	 * @param hTolerance the tolerance
     * @see #getHTolerance
	 */
	public void setHTolerance( float hTolerance ) {
		this.hTolerance = hTolerance;
	}
	
	/**
	 * Get the hue tolerance.
	 * @return the tolerance
     * @see #setHTolerance
	 */
	public float getHTolerance() {
		return hTolerance;
	}
	
	/**
	 * Set the saturation tolerance of the image in the range 0..1.
	 * @param sTolerance the tolerance
     * @see #getSTolerance
	 */
	public void setSTolerance( float sTolerance ) {
		this.sTolerance = sTolerance;
	}
	
	/**
	 * Get the saturation tolerance.
	 * @return the tolerance
     * @see #setSTolerance
	 */
	public float getSTolerance() {
		return sTolerance;
	}
	
	/**
	 * Set the brightness tolerance of the image in the range 0..1.
	 * @param bTolerance the tolerance
     * @see #getBTolerance
	 */
	public void setBTolerance( float bTolerance ) {
		this.bTolerance = bTolerance;
	}
	
	/**
	 * Get the brightness tolerance.
	 * @return the tolerance
     * @see #setBTolerance
	 */
	public float getBTolerance() {
		return bTolerance;
	}
	
    /**
     * Set the destination image.
     * @param destination the destination image
     * @see #getDestination
     */
	public void setDestination( BufferedImage destination ) {
		this.destination = destination;
	}
	
    /**
     * Get the destination image.
     * @return the destination image
     * @see #setDestination
     */
	public BufferedImage getDestination() {
		return destination;
	}
	
    /**
     * Get the clean image.
     * @param cleanImage the clean image
     * @see #getCleanImage
     */
	public void setCleanImage( BufferedImage cleanImage ) {
		this.cleanImage = cleanImage;
	}
	
    /**
     * Get the clean image.
     * @return the clean image
     * @see #setCleanImage
     */
	public BufferedImage getCleanImage() {
		return cleanImage;
	}
		
    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        int width = src.getWidth();
        int height = src.getHeight();
		int type = src.getType();
		WritableRaster srcRaster = src.getRaster();

        if ( dst == null )
            dst = createCompatibleDestImage( src, null );
		WritableRaster dstRaster = dst.getRaster();

        if ( destination != null && cleanImage != null ) {
            float[] hsb1 = null;
            float[] hsb2 = null;
            int[] inPixels = null;
            int[] outPixels = null;
            int[] cleanPixels = null;
            for ( int y = 0; y < height; y++ ) {
                inPixels = getRGB( src, 0, y, width, 1, inPixels );
                outPixels = getRGB( destination, 0, y, width, 1, outPixels );
                cleanPixels = getRGB( cleanImage, 0, y, width, 1, cleanPixels );
                for ( int x = 0; x < width; x++ ) {
                    int rgb1 = inPixels[x];
                    int out = outPixels[x];
                    int rgb2 = cleanPixels[x];

                    int r1 = (rgb1 >> 16) & 0xff;
                    int g1 = (rgb1 >> 8) & 0xff;
                    int b1 = rgb1 & 0xff;
                    int r2 = (rgb2 >> 16) & 0xff;
                    int g2 = (rgb2 >> 8) & 0xff;
                    int b2 = rgb2 & 0xff;
                    hsb1 = Color.RGBtoHSB( r1, b1, g1, hsb1 );
                    hsb2 = Color.RGBtoHSB( r2, b2, g2, hsb2 );
//                    int tolerance = (int)(255*tolerance);
//                    return Math.abs(r1-r2) <= tolerance && Math.abs(g1-g2) <= tolerance && Math.abs(b1-b2) <= tolerance;

 //                   if ( PixelUtils.nearColors( in, clean, (int)(255*tolerance) ) )
                    if ( Math.abs( hsb1[0] - hsb2[0] ) < hTolerance && Math.abs( hsb1[1] - hsb2[1] ) < sTolerance && Math.abs( hsb1[2] - hsb2[2] ) < bTolerance )
                        inPixels[x] = out;
                    else
                        inPixels[x] = rgb1;
                }
                setRGB( dst, 0, y, width, 1, inPixels );
            }
        }

        return dst;
    }

	public String toString() {
		return "Keying/Key...";
	}
}
