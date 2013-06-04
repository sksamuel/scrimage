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
 * A filter which distorts and image by performing coordinate conversions between rectangular and polar coordinates.
 */
public class PolarFilter extends TransformFilter {
	
	/**
     * Convert from rectangular to polar coordinates.
     */
    public final static int RECT_TO_POLAR = 0;

	/**
     * Convert from polar to rectangular coordinates.
     */
	public final static int POLAR_TO_RECT = 1;

	/**
     * Invert the image in a circle.
     */
	public final static int INVERT_IN_CIRCLE = 2;

	private int type;
	private float width, height;
	private float centreX, centreY;
	private float radius;

	/**
     * Construct a PolarFilter.
     */
    public PolarFilter() {
		this(RECT_TO_POLAR);
	}

	/**
     * Construct a PolarFilter.
     * @param type the distortion type
     */
	public PolarFilter(int type) {
		this.type = type;
		setEdgeAction(CLAMP);
	}

    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
		this.width = src.getWidth();
		this.height = src.getHeight();
		centreX = width/2;
		centreY = height/2;
		radius = Math.max(centreY, centreX);
		return super.filter( src, dst );
	}
	
	/**
     * Set the distortion type.
     * @param type the distortion type
     * @see #getType
     */
	public void setType(int type) {
		this.type = type;
	}

	/**
     * Get the distortion type.
     * @return the distortion type
     * @see #setType
     */
	public int getType() {
		return type;
	}

	private float sqr(float x) {
		return x*x;
	}

	protected void transformInverse(int x, int y, float[] out) {
		float theta, t;
		float m, xmax, ymax;
		float r = 0;
		
		switch (type) {
		case RECT_TO_POLAR:
			theta = 0;
			if (x >= centreX) {
				if (y > centreY) {
					theta = ImageMath.PI - (float)Math.atan(((float)(x - centreX))/((float)(y - centreY)));
					r = (float)Math.sqrt(sqr (x - centreX) + sqr (y - centreY));
				} else if (y < centreY) {
					theta = (float)Math.atan (((float)(x - centreX))/((float)(centreY - y)));
					r = (float)Math.sqrt (sqr (x - centreX) + sqr (centreY - y));
				} else {
					theta = ImageMath.HALF_PI;
					r = x - centreX;
				}
			} else if (x < centreX) {
				if (y < centreY) {
					theta = ImageMath.TWO_PI - (float)Math.atan (((float)(centreX -x))/((float)(centreY - y)));
					r = (float)Math.sqrt (sqr (centreX - x) + sqr (centreY - y));
				} else if (y > centreY) {
					theta = ImageMath.PI + (float)Math.atan (((float)(centreX - x))/((float)(y - centreY)));
					r = (float)Math.sqrt (sqr (centreX - x) + sqr (y - centreY));
				} else {
					theta = 1.5f * ImageMath.PI;
					r = centreX - x;
				}
			}
			if (x != centreX)
				m = Math.abs (((float)(y - centreY)) / ((float)(x - centreX)));
			else
				m = 0;
			
			if (m <= ((float)height / (float)width)) {
				if (x == centreX) {
					xmax = 0;
					ymax = centreY;
				} else {
					xmax = centreX;
					ymax = m * xmax;
				}
			} else {
				ymax = centreY;
				xmax = ymax / m;
			}
			
			out[0] = (width-1) - (width - 1)/ImageMath.TWO_PI * theta;
			out[1] = height * r / radius;
			break;
		case POLAR_TO_RECT:
			theta = x / width * ImageMath.TWO_PI;
			float theta2;

			if (theta >= 1.5f * ImageMath.PI)
				theta2 = ImageMath.TWO_PI - theta;
			else if (theta >= ImageMath.PI)
				theta2 = theta - ImageMath.PI;
			else if (theta >= 0.5f * ImageMath.PI)
				theta2 = ImageMath.PI - theta;
			else
				theta2 = theta;
	
			t = (float)Math.tan(theta2);
			if (t != 0)
				m = 1.0f / t;
			else
				m = 0;
	
			if (m <= ((float)(height) / (float)(width))) {
				if (theta2 == 0) {
					xmax = 0;
					ymax = centreY;
				} else {
					xmax = centreX;
					ymax = m * xmax;
				}
			} else {
				ymax = centreY;
				xmax = ymax / m;
			}
	
			r = radius * (float)(y / (float)(height));

			float nx = -r * (float)Math.sin(theta2);
			float ny = r * (float)Math.cos(theta2);
			
			if (theta >= 1.5f * ImageMath.PI) {
				out[0] = (float)centreX - nx;
				out[1] = (float)centreY - ny;
			} else if (theta >= Math.PI) {
				out[0] = (float)centreX - nx;
				out[1] = (float)centreY + ny;
			} else if (theta >= 0.5 * Math.PI) {
				out[0] = (float)centreX + nx;
				out[1] = (float)centreY + ny;
			} else {
				out[0] = (float)centreX + nx;
				out[1] = (float)centreY - ny;
			}
			break;
		case INVERT_IN_CIRCLE:
			float dx = x-centreX;
			float dy = y-centreY;
			float distance2 = dx*dx+dy*dy;
			out[0] = centreX + centreX*centreX * dx/distance2;
			out[1] = centreY + centreY*centreY * dy/distance2;
			break;
		}
	}

	public String toString() {
		return "Distort/Polar Coordinates...";
	}

}
