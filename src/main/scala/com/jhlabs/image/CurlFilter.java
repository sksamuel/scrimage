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
 * A page curl effect.
 */
public class CurlFilter extends TransformFilter {
	
	private float angle = 0;
	private float transition = 0.0f;

	private float width;
	private float height;
	private float radius;

	/**
	 * Construct a CurlFilter with no distortion.
	 */
	public CurlFilter() {
		setEdgeAction( ZERO );
	}

	public void setTransition( float transition ) {
		this.transition = transition;
	}
	
	public float getTransition() {
		return transition;
	}
	
	public void setAngle(float angle) {
		this.angle = angle;
	}
	
	public float getAngle() {
		return angle;
	}
	
	public void setRadius( float radius ) {
		this.radius = radius;
	}

	public float getRadius() {
		return radius;
	}
	
/*
    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
		this.width = src.getWidth();
		this.height = src.getHeight();
		return super.filter( src, dst );
	}
*/

	static class Sampler {
		private int edgeAction;
		private int width, height;
		private int[] inPixels;
		
		public Sampler( BufferedImage image ) {
			int width = image.getWidth();
			int height = image.getHeight();
			int type = image.getType();
			inPixels = ImageUtils.getRGB( image, 0, 0, width, height, null );
		}
		
		public int sample( float x, float y ) {
			int srcX = (int)Math.floor( x );
			int srcY = (int)Math.floor( y );
			float xWeight = x-srcX;
			float yWeight = y-srcY;
			int nw, ne, sw, se;

			if ( srcX >= 0 && srcX < width-1 && srcY >= 0 && srcY < height-1) {
				// Easy case, all corners are in the image
				int i = width*srcY + srcX;
				nw = inPixels[i];
				ne = inPixels[i+1];
				sw = inPixels[i+width];
				se = inPixels[i+width+1];
			} else {
				// Some of the corners are off the image
				nw = getPixel( inPixels, srcX, srcY, width, height );
				ne = getPixel( inPixels, srcX+1, srcY, width, height );
				sw = getPixel( inPixels, srcX, srcY+1, width, height );
				se = getPixel( inPixels, srcX+1, srcY+1, width, height );
			}
			return ImageMath.bilinearInterpolate(xWeight, yWeight, nw, ne, sw, se);
		}

		final private int getPixel( int[] pixels, int x, int y, int width, int height ) {
			if (x < 0 || x >= width || y < 0 || y >= height) {
				switch (edgeAction) {
				case ZERO:
				default:
					return 0;
				case WRAP:
					return pixels[(ImageMath.mod(y, height) * width) + ImageMath.mod(x, width)];
				case CLAMP:
					return pixels[(ImageMath.clamp(y, 0, height-1) * width) + ImageMath.clamp(x, 0, width-1)];
				}
			}
			return pixels[ y*width+x ];
		}
	}
	
    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        int width = src.getWidth();
        int height = src.getHeight();
		this.width = src.getWidth();
		this.height = src.getHeight();
		int type = src.getType();

		originalSpace = new Rectangle(0, 0, width, height);
		transformedSpace = new Rectangle(0, 0, width, height);
		transformSpace(transformedSpace);

        if ( dst == null ) {
            ColorModel dstCM = src.getColorModel();
			dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(transformedSpace.width, transformedSpace.height), dstCM.isAlphaPremultiplied(), null);
		}
		WritableRaster dstRaster = dst.getRaster();

		int[] inPixels = getRGB( src, 0, 0, width, height, null );

		if ( interpolation == NEAREST_NEIGHBOUR )
			return filterPixelsNN( dst, width, height, inPixels, transformedSpace );

		int srcWidth = width;
		int srcHeight = height;
		int srcWidth1 = width-1;
		int srcHeight1 = height-1;
		int outWidth = transformedSpace.width;
		int outHeight = transformedSpace.height;
		int outX, outY;
		int index = 0;
		int[] outPixels = new int[outWidth];

		outX = transformedSpace.x;
		outY = transformedSpace.y;
		float[] out = new float[4];

		for (int y = 0; y < outHeight; y++) {
			for (int x = 0; x < outWidth; x++) {
				transformInverse(outX+x, outY+y, out);
				int srcX = (int)Math.floor( out[0] );
				int srcY = (int)Math.floor( out[1] );
				float xWeight = out[0]-srcX;
				float yWeight = out[1]-srcY;
				int nw, ne, sw, se;

				if ( srcX >= 0 && srcX < srcWidth1 && srcY >= 0 && srcY < srcHeight1) {
					// Easy case, all corners are in the image
					int i = srcWidth*srcY + srcX;
					nw = inPixels[i];
					ne = inPixels[i+1];
					sw = inPixels[i+srcWidth];
					se = inPixels[i+srcWidth+1];
				} else {
					// Some of the corners are off the image
					nw = getPixel( inPixels, srcX, srcY, srcWidth, srcHeight );
					ne = getPixel( inPixels, srcX+1, srcY, srcWidth, srcHeight );
					sw = getPixel( inPixels, srcX, srcY+1, srcWidth, srcHeight );
					se = getPixel( inPixels, srcX+1, srcY+1, srcWidth, srcHeight );
				}
				int rgb = ImageMath.bilinearInterpolate(xWeight, yWeight, nw, ne, sw, se);
				int r = (rgb >> 16) & 0xff;
				int g = (rgb >> 8) & 0xff;
				int b = rgb & 0xff;
				float shade = out[2];
				r = (int)(r * shade);
				g = (int)(g * shade);
				b = (int)(b * shade);
				rgb = (rgb & 0xff000000) | (r << 16) | (g << 8) | b;
				if ( out[3] != 0 )
					outPixels[x] = PixelUtils.combinePixels( rgb, inPixels[srcWidth*y + x], PixelUtils.NORMAL );
				else
					outPixels[x] = rgb;
			}
			setRGB( dst, 0, y, transformedSpace.width, 1, outPixels );
		}
		return dst;
	}

	final private int getPixel( int[] pixels, int x, int y, int width, int height ) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			switch (edgeAction) {
			case ZERO:
			default:
				return 0;
			case WRAP:
				return pixels[(ImageMath.mod(y, height) * width) + ImageMath.mod(x, width)];
			case CLAMP:
				return pixels[(ImageMath.clamp(y, 0, height-1) * width) + ImageMath.clamp(x, 0, width-1)];
			}
		}
		return pixels[ y*width+x ];
	}

	protected void transformInverse(int x, int y, float[] out) {
/*Fisheye
		float mirrorDistance = width*centreX;
		float mirrorRadius = width*centreY;
		float cx = width*.5f;
		float cy = height*.5f;
		float dx = x-cx;
		float dy = y-cy;
		float r2 = dx*dx+dy*dy;
		float r = (float)Math.sqrt( r2 );
		float phi = (float)(Math.PI*.5-Math.asin( Math.sqrt( mirrorRadius*mirrorRadius-r2 )/mirrorRadius ));
		r = r > mirrorRadius ? width : mirrorDistance * (float)Math.tan( phi );
		phi = (float)Math.atan2( dy, dx );
		out[0] = cx + r*(float)Math.cos( phi );
		out[1] = cy + r*(float)Math.sin( phi );
*/
		float t = transition;
		float px = x, py = y;
		float s = (float)Math.sin( angle );
		float c = (float)Math.cos( angle );
		float tx = t*width;
		tx = t * (float)Math.sqrt( width*width + height*height);

		// Start from the correct corner according to the angle
		float xoffset = c < 0 ? width : 0;
		float yoffset = s < 0 ? height : 0;

		// Transform into unrotated coordinates
		px -= xoffset;
		py -= yoffset;

		float qx = px * c + py * s;
		float qy = -px * s + py * c;

		boolean outside = qx < tx;
		boolean unfolded = qx > tx*2;
		boolean oncurl = !(outside || unfolded);

		qx = qx > tx*2 ? qx : 2*tx-qx;
	
		// Transform back into rotated coordinates
		px = qx * c - qy * s;
		py = qx * s + qy * c;
		px += xoffset;
		py += yoffset;

		// See if we're off the edge of the page
		boolean offpage = px < 0 || py < 0 || px >= width || py >= height;

		// If we're off the edge, but in the curl...
		if ( offpage && oncurl ) {
			px = x;
			py = y;
		}
	
		// Shade the curl
		float shade = !offpage && oncurl ? 1.9f * (1.0f-(float)Math.cos( Math.exp((qx-tx)/radius) )) : 0;
		out[2] = 1-shade;

		if ( outside ) {
			out[0] = out[1] = -1;
		} else {
			out[0] = px;
			out[1] = py;
		}
		
		out[3] = !offpage && oncurl ? 1 : 0;
	}

	public String toString() {
		return "Distort/Curl...";
	}

}
