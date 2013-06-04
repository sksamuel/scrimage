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
import java.awt.geom.*;

/**
 * A filter which performs a "smart blur". i.e. a blur which blurs smotth parts of the image while preserving edges.
 */
public class SmartBlurFilter extends AbstractBufferedImageOp {

	private int hRadius = 5;
	private int vRadius = 5;
	private int threshold = 10;
	
    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        int width = src.getWidth();
        int height = src.getHeight();

        if ( dst == null )
            dst = createCompatibleDestImage( src, null );

        int[] inPixels = new int[width*height];
        int[] outPixels = new int[width*height];
        getRGB( src, 0, 0, width, height, inPixels );

		Kernel kernel = GaussianFilter.makeKernel(hRadius);
		thresholdBlur( kernel, inPixels, outPixels, width, height, true );
		thresholdBlur( kernel, outPixels, inPixels, height, width, true );

        setRGB( dst, 0, 0, width, height, inPixels );
        return dst;
    }

	/**
	 * Convolve with a kernel consisting of one row
	 */
	private void thresholdBlur(Kernel kernel, int[] inPixels, int[] outPixels, int width, int height, boolean alpha) {
		int index = 0;
		float[] matrix = kernel.getKernelData( null );
		int cols = kernel.getWidth();
		int cols2 = cols/2;

		for (int y = 0; y < height; y++) {
			int ioffset = y*width;
            int outIndex = y;
			for (int x = 0; x < width; x++) {
				float r = 0, g = 0, b = 0, a = 0;
				int moffset = cols2;

                int rgb1 = inPixels[ioffset+x];
                int a1 = (rgb1 >> 24) & 0xff;
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = rgb1 & 0xff;
				float af = 0, rf = 0, gf = 0, bf = 0;
                for (int col = -cols2; col <= cols2; col++) {
					float f = matrix[moffset+col];

					if (f != 0) {
						int ix = x+col;
						if (!(0 <= ix && ix < width))
							ix = x;
						int rgb2 = inPixels[ioffset+ix];
                        int a2 = (rgb2 >> 24) & 0xff;
                        int r2 = (rgb2 >> 16) & 0xff;
                        int g2 = (rgb2 >> 8) & 0xff;
                        int b2 = rgb2 & 0xff;

						int d;
                        d = a1-a2;
                        if ( d >= -threshold && d <= threshold ) {
                            a += f * a2;
                            af += f;
                        }
                        d = r1-r2;
                        if ( d >= -threshold && d <= threshold ) {
                            r += f * r2;
                            rf += f;
                        }
                        d = g1-g2;
                        if ( d >= -threshold && d <= threshold ) {
                            g += f * g2;
                            gf += f;
                        }
                        d = b1-b2;
                        if ( d >= -threshold && d <= threshold ) {
                            b += f * b2;
                            bf += f;
                        }
					}
				}
                a = af == 0 ? a1 : a/af;
                r = rf == 0 ? r1 : r/rf;
                g = gf == 0 ? g1 : g/gf;
                b = bf == 0 ? b1 : b/bf;
				int ia = alpha ? PixelUtils.clamp((int)(a+0.5)) : 0xff;
				int ir = PixelUtils.clamp((int)(r+0.5));
				int ig = PixelUtils.clamp((int)(g+0.5));
				int ib = PixelUtils.clamp((int)(b+0.5));
				outPixels[outIndex] = (ia << 24) | (ir << 16) | (ig << 8) | ib;
                outIndex += height;
			}
		}
	}

	/**
	 * Set the horizontal size of the blur.
	 * @param hRadius the radius of the blur in the horizontal direction
     * @min-value 0
     * @see #getHRadius
	 */
	public void setHRadius(int hRadius) {
		this.hRadius = hRadius;
	}
	
	/**
	 * Get the horizontal size of the blur.
	 * @return the radius of the blur in the horizontal direction
     * @see #setHRadius
	 */
	public int getHRadius() {
		return hRadius;
	}
	
	/**
	 * Set the vertical size of the blur.
	 * @param vRadius the radius of the blur in the vertical direction
     * @min-value 0
     * @see #getVRadius
	 */
	public void setVRadius(int vRadius) {
		this.vRadius = vRadius;
	}
	
	/**
	 * Get the vertical size of the blur.
	 * @return the radius of the blur in the vertical direction
     * @see #setVRadius
	 */
	public int getVRadius() {
		return vRadius;
	}
	
	/**
	 * Set the radius of the effect.
	 * @param radius the radius
     * @min-value 0
     * @see #getRadius
	 */
	public void setRadius(int radius) {
		this.hRadius = this.vRadius = radius;
	}
	
	/**
	 * Get the radius of the effect.
	 * @return the radius
     * @see #setRadius
	 */
	public int getRadius() {
		return hRadius;
	}
	
	/**
     * Set the threshold value.
     * @param threshold the threshold value
     * @see #getThreshold
     */
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
	
	/**
     * Get the threshold value.
     * @return the threshold value
     * @see #setThreshold
     */
	public int getThreshold() {
		return threshold;
	}
	
	public String toString() {
		return "Blur/Smart Blur...";
	}
}
