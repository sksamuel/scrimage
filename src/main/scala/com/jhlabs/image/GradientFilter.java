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
 * A filter which draws a coloured gradient. This is largely superceded by GradientPaint in Java1.2, but does provide a few
 * more gradient options.
 */
public class GradientFilter extends AbstractBufferedImageOp {

	public final static int LINEAR = 0;
	public final static int BILINEAR = 1;
	public final static int RADIAL = 2;
	public final static int CONICAL = 3;
	public final static int BICONICAL = 4;
	public final static int SQUARE = 5;

	public final static int INT_LINEAR = 0;
	public final static int INT_CIRCLE_UP = 1;
	public final static int INT_CIRCLE_DOWN = 2;
	public final static int INT_SMOOTH = 3;

	private float angle = 0;
	private int color1 = 0xff000000;
	private int color2 = 0xffffffff;
	private Point p1 = new Point(0, 0), p2 = new Point(64, 64);
	private boolean repeat = false;
	private float x1;
	private float y1;
	private float dx;
	private float dy;
	private Colormap colormap = null;
	private int type;
	private int interpolation = INT_LINEAR;
	private int paintMode = PixelUtils.NORMAL;

	public GradientFilter() {
	}

	public GradientFilter(Point p1, Point p2, int color1, int color2, boolean repeat, int type, int interpolation) {
		this.p1 = p1;
		this.p2 = p2;
		this.color1 = color1;
		this.color2 = color2;
		this.repeat = repeat;
		this.type = type;
		this.interpolation = interpolation;
		colormap = new LinearColormap(color1, color2);
	}

	public void setPoint1(Point point1) {
		this.p1 = point1;
	}

	public Point getPoint1() {
		return p1;
	}

	public void setPoint2(Point point2) {
		this.p2 = point2;
	}

	public Point getPoint2() {
		return p2;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setInterpolation(int interpolation) {
		this.interpolation = interpolation;
	}

	public int getInterpolation() {
		return interpolation;
	}

	/**
     * Specifies the angle of the texture.
     * @param angle the angle of the texture.
     * @angle
     * @see #getAngle
     */
	public void setAngle(float angle) {
		this.angle = angle;
		p2 = new Point((int)(64*Math.cos(angle)), (int)(64*Math.sin(angle)));
	}
	
	/**
     * Returns the angle of the texture.
     * @return the angle of the texture.
     * @see #setAngle
     */
	public float getAngle() {
		return angle;
	}
	
    /**
     * Set the colormap to be used for the filter.
     * @param colormap the colormap
     * @see #getColormap
     */
	public void setColormap(Colormap colormap) {
		this.colormap = colormap;
	}
	
    /**
     * Get the colormap to be used for the filter.
     * @return the colormap
     * @see #setColormap
     */
	public Colormap getColormap() {
		return colormap;
	}
	
	public void setPaintMode(int paintMode) {
		this.paintMode = paintMode;
	}

	public int getPaintMode() {
		return paintMode;
	}

    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        int width = src.getWidth();
        int height = src.getHeight();

        if ( dst == null )
            dst = createCompatibleDestImage( src, null );

		int rgb1, rgb2;
		float x1, y1, x2, y2;
		x1 = p1.x;
		x2 = p2.x;

		if (x1 > x2 && type != RADIAL) {
			y1 = x1;
			x1 = x2;
			x2 = y1;
			y1 = p2.y;
			y2 = p1.y;
			rgb1 = color2;
			rgb2 = color1;
		} else {
			y1 = p1.y;
			y2 = p2.y;
			rgb1 = color1;
			rgb2 = color2;
		}
		float dx = x2 - x1;
		float dy = y2 - y1;
		float lenSq = dx * dx + dy * dy;
		this.x1 = x1;
		this.y1 = y1;
		if (lenSq >= Float.MIN_VALUE) {
			dx = dx / lenSq;
			dy = dy / lenSq;
			if (repeat) {
				dx = dx % 1.0f;
				dy = dy % 1.0f;
			}
		}
		this.dx = dx;
		this.dy = dy;

        int[] pixels = new int[width];
        for (int y = 0; y < height; y++ ) {
			getRGB( src, 0, y, width, 1, pixels );
			switch (type) {
			case LINEAR:
			case BILINEAR:
				linearGradient(pixels, y, width, 1);
				break;
			case RADIAL:
				radialGradient(pixels, y, width, 1);
				break;
			case CONICAL:
			case BICONICAL:
				conicalGradient(pixels, y, width, 1);
				break;
			case SQUARE:
				squareGradient(pixels, y, width, 1);
				break;
			}
			setRGB( dst, 0, y, width, 1, pixels );
        }
		return dst;
    }

	private void repeatGradient(int[] pixels, int w, int h, float rowrel, float dx, float dy) {
		int off = 0;
		for (int y = 0; y < h; y++) {
			float colrel = rowrel;
			int j = w;
			int rgb;
			while (--j >= 0) {
				if (type == BILINEAR)
					rgb = colormap.getColor(map(ImageMath.triangle(colrel)));
				else
					rgb = colormap.getColor(map(ImageMath.mod(colrel, 1.0f)));
				pixels[off] = PixelUtils.combinePixels(rgb, pixels[off], paintMode);
				off++;
				colrel += dx;
			}
			rowrel += dy;
		}
	}

	private void singleGradient(int[] pixels, int w, int h, float rowrel, float dx, float dy) {
		int off = 0;
		for (int y = 0; y < h; y++) {
			float colrel = rowrel;
			int j = w;
			int rgb;
			if (colrel <= 0.0) {
				rgb = colormap.getColor(0);
				do {
					pixels[off] = PixelUtils.combinePixels(rgb, pixels[off], paintMode);
					off++;
					colrel += dx;
				} while (--j > 0 && colrel <= 0.0);
			}
			while (colrel < 1.0 && --j >= 0) {
				if (type == BILINEAR)
					rgb = colormap.getColor(map(ImageMath.triangle(colrel)));
				else
					rgb = colormap.getColor(map(colrel));
				pixels[off] = PixelUtils.combinePixels(rgb, pixels[off], paintMode);
				off++;
				colrel += dx;
			}
			if (j > 0) {
				if (type == BILINEAR)
					rgb = colormap.getColor(0.0f);
				else
					rgb = colormap.getColor(1.0f);
				do {
					pixels[off] = PixelUtils.combinePixels(rgb, pixels[off], paintMode);
					off++;
				} while (--j > 0);
			}
			rowrel += dy;
		}
	}

	private void linearGradient(int[] pixels, int y, int w, int h) {
		int x = 0;
		float rowrel = (x - x1) * dx + (y - y1) * dy;
		if (repeat)
			repeatGradient(pixels, w, h, rowrel, dx, dy);
		else
			singleGradient(pixels, w, h, rowrel, dx, dy);
	}
	
	private void radialGradient(int[] pixels, int y, int w, int h) {
		int off = 0;
		float radius = distance(p2.x-p1.x, p2.y-p1.y);
		for (int x = 0; x < w; x++) {
			float distance = distance(x-p1.x, y-p1.y);
			float ratio = distance / radius;
			if (repeat)
				ratio = ratio % 2;
			else if (ratio > 1.0)
				ratio = 1.0f;
			int rgb = colormap.getColor(map(ratio));
			pixels[off] = PixelUtils.combinePixels(rgb, pixels[off], paintMode);
			off++;
		}
	}
	
	private void squareGradient(int[] pixels, int y, int w, int h) {
		int off = 0;
		float radius = Math.max(Math.abs(p2.x-p1.x), Math.abs(p2.y-p1.y));
		for (int x = 0; x < w; x++) {
			float distance = Math.max(Math.abs(x-p1.x), Math.abs(y-p1.y));
			float ratio = distance / radius;
			if (repeat)
				ratio = ratio % 2;
			else if (ratio > 1.0)
				ratio = 1.0f;
			int rgb = colormap.getColor(map(ratio));
			pixels[off] = PixelUtils.combinePixels(rgb, pixels[off], paintMode);
			off++;
		}
	}
	
	private void conicalGradient(int[] pixels, int y, int w, int h) {
		int off = 0;
		float angle0 = (float)Math.atan2(p2.x-p1.x, p2.y-p1.y);
		for (int x = 0; x < w; x++) {
			float angle = (float)(Math.atan2(x-p1.x, y-p1.y) - angle0) / (ImageMath.TWO_PI);
			angle += 1.0f;
			angle %= 1.0f;
			if (type == BICONICAL)
				angle = ImageMath.triangle(angle);
			int rgb = colormap.getColor(map(angle));
			pixels[off] = PixelUtils.combinePixels(rgb, pixels[off], paintMode);
			off++;
		}
	}
	
	private float map(float v) {
		if (repeat)
			v = v > 1.0 ? 2.0f-v : v;
		switch (interpolation) {
		case INT_CIRCLE_UP:
			v = ImageMath.circleUp(ImageMath.clamp(v, 0.0f, 1.0f));
			break;
		case INT_CIRCLE_DOWN:
			v = ImageMath.circleDown(ImageMath.clamp(v, 0.0f, 1.0f));
			break;
		case INT_SMOOTH:
			v = ImageMath.smoothStep(0, 1, v);
			break;
		}
		return v;
	}
	
	private float distance(float a, float b) {
		return (float)Math.sqrt(a*a+b*b);
	}
	
	public String toString() {
		return "Other/Gradient Fill...";
	}
}
