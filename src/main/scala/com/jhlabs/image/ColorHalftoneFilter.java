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
import java.awt.geom.*;
import java.awt.image.*;

/**
 * A Filter to pixellate images.
 */
public class ColorHalftoneFilter extends AbstractBufferedImageOp {
	
	private float dotRadius = 2;
    private float cyanScreenAngle = (float)Math.toRadians( 108 );
    private float magentaScreenAngle = (float)Math.toRadians( 162 );
    private float yellowScreenAngle = (float)Math.toRadians( 90 );

	public ColorHalftoneFilter() {
	}

	/**
	 * Set the pixel block size.
	 * @param dotRadius the number of pixels along each block edge
     * @min-value 1
     * @max-value 100+
     * @see #getdotRadius
	 */
	public void setdotRadius( float dotRadius ) {
		this.dotRadius = dotRadius;
	}

	/**
	 * Get the pixel block size.
	 * @return the number of pixels along each block edge
     * @see #setdotRadius
	 */
	public float getdotRadius() {
		return dotRadius;
	}

	/**
	 * Get the cyan screen angle.
	 * @return the cyan screen angle (in radians)
     * @see #setCyanScreenAngle
	 */
	public float getCyanScreenAngle() {
		return cyanScreenAngle;
	}

	/**
	 * Set the cyan screen angle.
	 * @param cyanScreenAngle the cyan screen angle (in radians)
     * @see #getCyanScreenAngle
	 */
	public void setCyanScreenAngle( float cyanScreenAngle ) {
		this.cyanScreenAngle = cyanScreenAngle;
	}

	/**
	 * Get the magenta screen angle.
	 * @return the magenta screen angle (in radians)
     * @see #setMagentaScreenAngle
	 */
	public float getMagentaScreenAngle() {
		return magentaScreenAngle;
	}

	/**
	 * Set the magenta screen angle.
	 * @param magentaScreenAngle the magenta screen angle (in radians)
     * @see #getMagentaScreenAngle
	 */
	public void setMagentaScreenAngle( float magentaScreenAngle ) {
		this.magentaScreenAngle = magentaScreenAngle;
	}

	/**
	 * Get the yellow screen angle.
	 * @return the yellow screen angle (in radians)
     * @see #setYellowScreenAngle
	 */
	public float getYellowScreenAngle() {
		return yellowScreenAngle;
	}

	/**
	 * Set the yellow screen angle.
	 * @param yellowScreenAngle the yellow screen angle (in radians)
     * @see #getYellowScreenAngle
	 */
	public void setYellowScreenAngle( float yellowScreenAngle ) {
		this.yellowScreenAngle = yellowScreenAngle;
	}

    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        int width = src.getWidth();
        int height = src.getHeight();
		int type = src.getType();
		WritableRaster srcRaster = src.getRaster();

        if ( dst == null )
            dst = createCompatibleDestImage( src, null );

        float gridSize = 2*dotRadius*1.414f;
        float[] angles = { cyanScreenAngle, magentaScreenAngle, yellowScreenAngle };
        float[] mx = new float[] { 0, -1, 1,  0, 0 };
        float[] my = new float[] { 0, 0, 0, -1, 1 };
        float halfGridSize = (float)gridSize/2;
        int[] outPixels = new int[width];
        int[] inPixels = getRGB( src, 0, 0, width, height, null );
        for ( int y = 0; y < height; y++ ) {
            for ( int x = 0, ix = y*width; x < width; x++, ix++ )
                outPixels[x] = (inPixels[ix] & 0xff000000) | 0xffffff;
            for ( int channel = 0; channel < 3; channel++ ) {
                int shift = 16-8*channel;
                int mask = 0x000000ff << shift;
                float angle = angles[channel];
                float sin = (float)Math.sin( angle );
                float cos = (float)Math.cos( angle );

                for ( int x = 0; x < width; x++ ) {
                    // Transform x,y into halftone screen coordinate space
                    float tx = x*cos + y*sin;
                    float ty = -x*sin + y*cos;
                    
                    // Find the nearest grid point
                    tx = tx-ImageMath.mod( tx-halfGridSize, gridSize )+halfGridSize;
                    ty = ty-ImageMath.mod( ty-halfGridSize, gridSize )+halfGridSize;

                    float f = 1;

                    // TODO: Efficiency warning: Because the dots overlap, we need to check neighbouring grid squares.
                    // We check all four neighbours, but in practice only one can ever overlap any given point.
                    for ( int i = 0; i < 5; i++ ) {
                        // Find neigbouring grid point
                        float ttx = tx + mx[i]*gridSize;
                        float tty = ty + my[i]*gridSize;
                        // Transform back into image space
                        float ntx = ttx*cos - tty*sin;
                        float nty = ttx*sin + tty*cos;
                        // Clamp to the image
                        int nx = ImageMath.clamp( (int)ntx, 0, width-1 );
                        int ny = ImageMath.clamp( (int)nty, 0, height-1 );
                        int argb = inPixels[ny*width+nx];
                        int nr = (argb >> shift) & 0xff;
                        float l = nr/255.0f;
                        l = 1-l*l;
                        l *= halfGridSize*1.414;
                        float dx = x-ntx;
                        float dy = y-nty;
                        float dx2 = dx*dx;
                        float dy2 = dy*dy;
                        float R = (float)Math.sqrt( dx2+dy2 );
                        float f2 = 1-ImageMath.smoothStep( R, R+1, l );
                        f = Math.min( f, f2 );
                    }

                    int v = (int)(255 * f);
                    v <<= shift;
                    v ^= ~mask;
					v |= 0xff000000;
                    outPixels[x] &= v;
                }
            }
			setRGB( dst, 0, y, width, 1, outPixels );
        }

        return dst;
    }

	public String toString() {
		return "Pixellate/Color Halftone...";
	}
}

