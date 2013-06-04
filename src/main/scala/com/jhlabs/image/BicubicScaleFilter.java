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
 * Scales an image using bi-cubic interpolation, which can't be done with AffineTransformOp.
 */
public class BicubicScaleFilter extends AbstractBufferedImageOp {

	private int width;
	private int height;

	/**
     * Construct a BicubicScaleFilter which resizes to 32x32 pixels.
     */
    public BicubicScaleFilter() {
		this(32, 32);
	}

	/**
	 * Constructor for a filter which scales the input image to the given width and height using bicubic interpolation.
	 * Unfortunately, it appears that bicubic actually looks worse than bilinear interpolation on most Java implementations,
	 * but you can be the judge.
     * @param width the width of the output image
     * @param height the height of the output image
	 */
	public BicubicScaleFilter( int width, int height ) {
		this.width = width;
		this.height = height;
	}

    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        int w = src.getWidth();
        int h = src.getHeight();

		if ( dst == null ) {
			ColorModel dstCM = src.getColorModel();
			dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(width, height), dstCM.isAlphaPremultiplied(), null);
		}

		Graphics2D g = dst.createGraphics();
		g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
		g.drawImage( src, 0, 0, width, height, null );
		g.dispose();

        return dst;
    }

	public String toString() {
		return "Distort/Bicubic Scale";
	}

}
