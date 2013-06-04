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
 * A filter which uses a another image as a ask to produce a halftoning effect.
 */
public class HalftoneFilter extends AbstractBufferedImageOp {
	
	private float softness = 0.1f;
	private boolean invert;
	private boolean monochrome;
	private BufferedImage mask;

	public HalftoneFilter() {
	}

	/**
	 * Set the softness of the effect in the range 0..1.
	 * @param softness the softness
     * @min-value 0
     * @max-value 1
     * @see #getSoftness
	 */
	public void setSoftness( float softness ) {
		this.softness = softness;
	}
	
	/**
	 * Get the softness of the effect.
	 * @return the softness
     * @see #setSoftness
	 */
	public float getSoftness() {
		return softness;
	}
	
	/**
	 * Set the halftone mask.
	 * @param mask the mask
     * @see #getMask
	 */
	public void setMask( BufferedImage mask ) {
		this.mask = mask;
	}
	
	/**
	 * Get the halftone mask.
	 * @return the mask
     * @see #setMask
	 */
	public BufferedImage getMask() {
		return mask;
	}
	
	public void setInvert( boolean invert ) {
		this.invert = invert;
	}
	
	public boolean getInvert() {
		return invert;
	}
	
	/**
	 * Set whether to do monochrome halftoning.
	 * @param monochrome true for monochrome halftoning
     * @see #getMonochrome
	 */
	public void setMonochrome(boolean monochrome) {
		this.monochrome = monochrome;
	}
	
	/**
	 * Get whether to do monochrome halftoning.
	 * @return true for monochrome halftoning
     * @see #setMonochrome
	 */
	public boolean getMonochrome() {
		return monochrome;
	}

    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        int width = src.getWidth();
        int height = src.getHeight();

        if ( dst == null )
            dst = createCompatibleDestImage( src, null );
		if ( mask == null )
			return dst;

        int maskWidth = mask.getWidth();
        int maskHeight = mask.getHeight();

        float s = 255*softness;

		int[] inPixels = new int[width];
		int[] maskPixels = new int[maskWidth];

        for ( int y = 0; y < height; y++ ) {
			getRGB( src, 0, y, width, 1, inPixels );
			getRGB( mask, 0, y % maskHeight, maskWidth, 1, maskPixels );

			for ( int x = 0; x < width; x++ ) {
				int maskRGB = maskPixels[x % maskWidth];
				int inRGB = inPixels[x];
				if ( invert )
                    maskRGB ^= 0xffffff;
                if ( monochrome ) {
                    int v = PixelUtils.brightness( maskRGB );
                    int iv = PixelUtils.brightness( inRGB );
                    float f = 1-ImageMath.smoothStep( iv-s, iv+s, v );
                    int a = (int)(255 * f);
                    inPixels[x] = (inRGB & 0xff000000) | (a << 16) | (a << 8) | a;
                } else {
                    int ir = (inRGB >> 16) & 0xff;
                    int ig = (inRGB >> 8) & 0xff;
                    int ib = inRGB & 0xff;
                    int mr = (maskRGB >> 16) & 0xff;
                    int mg = (maskRGB >> 8) & 0xff;
                    int mb = maskRGB & 0xff;
                    int r = (int)(255 * (1-ImageMath.smoothStep( ir-s, ir+s, mr )));
                    int g = (int)(255 * (1-ImageMath.smoothStep( ig-s, ig+s, mg )));
                    int b = (int)(255 * (1-ImageMath.smoothStep( ib-s, ib+s, mb )));
                    inPixels[x] = (inRGB & 0xff000000) | (r << 16) | (g << 8) | b;
                }
            }

			setRGB( dst, 0, y, width, 1, inPixels );
        }

        return dst;
    }

	public String toString() {
		return "Stylize/Halftone...";
	}
}
