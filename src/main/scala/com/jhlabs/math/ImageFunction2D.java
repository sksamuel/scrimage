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

package com.jhlabs.math;

import java.awt.*;
import java.awt.image.*;
import com.jhlabs.image.*;

public class ImageFunction2D implements Function2D {

	public final static int ZERO = 0;
	public final static int CLAMP = 1;
	public final static int WRAP = 2;
	
	protected int[] pixels;
	protected int width;
	protected int height;
	protected int edgeAction = ZERO;
	protected boolean alpha = false;
	
	public ImageFunction2D(BufferedImage image) {
		this(image, false);
	}
	
	public ImageFunction2D(BufferedImage image, boolean alpha) {
		this(image, ZERO, alpha);
	}
	
	public ImageFunction2D(BufferedImage image, int edgeAction, boolean alpha) {
		init( getRGB( image, 0, 0, image.getWidth(), image.getHeight(), null), image.getWidth(), image.getHeight(), edgeAction, alpha);
	}
	
	public ImageFunction2D(int[] pixels, int width, int height, int edgeAction, boolean alpha) {
		init(pixels, width, height, edgeAction, alpha);
	}
	
 	public ImageFunction2D(Image image) {
		this( image, ZERO, false );
 	}
	
	public ImageFunction2D(Image image, int edgeAction, boolean alpha) {
 		PixelGrabber pg = new PixelGrabber(image, 0, 0, -1, -1, null, 0, -1);
 		try {
 			pg.grabPixels();
 		} catch (InterruptedException e) {
 			throw new RuntimeException("interrupted waiting for pixels!");
 		}
 		if ((pg.status() & ImageObserver.ABORT) != 0) {
 			throw new RuntimeException("image fetch aborted");
 		}
 		init((int[])pg.getPixels(), pg.getWidth(), pg.getHeight(), edgeAction, alpha);
  	}

	/**
	 * A convenience method for getting ARGB pixels from an image. This tries to avoid the performance
	 * penalty of BufferedImage.getRGB unmanaging the image.
	 */
	public int[] getRGB( BufferedImage image, int x, int y, int width, int height, int[] pixels ) {
		int type = image.getType();
		if ( type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB )
			return (int [])image.getRaster().getDataElements( x, y, width, height, pixels );
		return image.getRGB( x, y, width, height, pixels, 0, width );
    }

	public void init(int[] pixels, int width, int height, int edgeAction, boolean alpha) {
		this.pixels = pixels;
		this.width = width;
		this.height = height;
		this.edgeAction = edgeAction;
		this.alpha = alpha;
	}
	
	public float evaluate(float x, float y) {
		int ix = (int)x;
		int iy = (int)y;
		if (edgeAction == WRAP) {
			ix = ImageMath.mod(ix, width);
			iy = ImageMath.mod(iy, height);
		} else if (ix < 0 || iy < 0 || ix >= width || iy >= height) {
			if (edgeAction == ZERO)
				return 0;
			if (ix < 0)
				ix = 0;
			else if (ix >= width)
				ix = width-1;
			if (iy < 0)
				iy = 0;
			else if (iy >= height)
				iy = height-1;
		}
		return alpha ? ((pixels[iy*width+ix] >> 24) & 0xff) / 255.0f : PixelUtils.brightness(pixels[iy*width+ix]) / 255.0f;
	}
	
	public void setEdgeAction(int edgeAction) {
		this.edgeAction = edgeAction;
	}

	public int getEdgeAction() {
		return edgeAction;
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int[] getPixels() {
		return pixels;
	}
}

