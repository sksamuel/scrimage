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
 * Scales an image using the area-averaging algorithm, which can't be done with AffineTransformOp.
 */
public class ScaleFilter extends AbstractBufferedImageOp {

	private int width;
	private int height;

    /**
     * Construct a ScaleFilter.
     */
	public ScaleFilter() {
		this(32, 32);
	}

    /**
     * Construct a ScaleFilter.
     * @param width the width to scale to
     * @param height the height to scale to
     */
	public ScaleFilter( int width, int height ) {
		this.width = width;
		this.height = height;
	}

    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
		if ( dst == null ) {
			ColorModel dstCM = src.getColorModel();
			dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster( width, height ), dstCM.isAlphaPremultiplied(), null);
		}

		Image scaleImage = src.getScaledInstance( width, height, Image.SCALE_AREA_AVERAGING );
		Graphics2D g = dst.createGraphics();
		g.drawImage( scaleImage, 0, 0, width, height, null );
		g.dispose();

        return dst;
    }

	public String toString() {
		return "Distort/Scale";
	}

}
