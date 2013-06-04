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
import com.jhlabs.composite.*;

/**
 * Edge detection by difference of Gaussians.
 * @author Jerry Huxtable
 */
public class DoGFilter extends AbstractBufferedImageOp {

	private float radius1 = 1;
	private float radius2 = 2;
    private boolean normalize = true;
    private boolean invert;
	
	public DoGFilter() {
	}
	
	/**
	 * Set the radius of the kernel, and hence the amount of blur. The bigger the radius, the longer this filter will take.
	 * @param radius the radius of the blur in pixels.
     * @min-value 0
     * @max-value 100+
     * @see #getRadius
	 */
	public void setRadius1(float radius1) {
		this.radius1 = radius1;
	}
	
	/**
	 * Get the radius of the kernel.
	 * @return the radius
     * @see #setRadius
	 */
	public float getRadius1() {
		return radius1;
	}

	/**
	 * Set the radius of the kernel, and hence the amount of blur. The bigger the radius, the longer this filter will take.
	 * @param radius the radius of the blur in pixels.
     * @min-value 0
     * @max-value 100+
     * @see #getRadius
	 */
	public void setRadius2(float radius2) {
		this.radius2 = radius2;
	}
	
	/**
	 * Get the radius of the kernel.
	 * @return the radius
     * @see #setRadius
	 */
	public float getRadius2() {
		return radius2;
	}
	
    public void setNormalize( boolean normalize ) {
        this.normalize = normalize;
    }
    
    public boolean getNormalize() {
        return normalize;
    }
    
    public void setInvert( boolean invert ) {
        this.invert = invert;
    }
    
    public boolean getInvert() {
        return invert;
    }
    
    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage image1 = new BoxBlurFilter( radius1, radius1, 3 ).filter( src, null );
        BufferedImage image2 = new BoxBlurFilter( radius2, radius2, 3 ).filter( src, null );
        Graphics2D g2d = image2.createGraphics();
        g2d.setComposite( new SubtractComposite( 1.0f ) );
        g2d.drawImage( image1, 0, 0, null );
        g2d.dispose();
        if ( normalize && radius1 != radius2 ) {
            int[] pixels = null;
            int max = 0;
            for ( int y = 0; y < height; y++ ) {
                pixels = getRGB( image2, 0, y, width, 1, pixels );
                for ( int x = 0; x < width; x++ ) {
                    int rgb = pixels[x];
                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;
                    if ( r > max )
                        max = r;
                    if ( g > max )
                        max = g;
                    if ( b > max )
                        max = b;
                }
            }

            for ( int y = 0; y < height; y++ ) {
                pixels = getRGB( image2, 0, y, width, 1, pixels );
                for ( int x = 0; x < width; x++ ) {
                    int rgb = pixels[x];
                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;
                    r = r * 255 / max;
                    g = g * 255 / max;
                    b = b * 255 / max;
                    pixels[x] = (rgb & 0xff000000) | (r << 16) | (g << 8) | b;
                }
                setRGB( image2, 0, y, width, 1, pixels );
            }

        }

        if ( invert )
            image2 = new InvertFilter().filter( image2, image2 );

        return image2;
    }

	public String toString() {
		return "Edges/Difference of Gaussians...";
	}
}
