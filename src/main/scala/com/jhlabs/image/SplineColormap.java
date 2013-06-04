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

import java.awt.Color;
import java.util.Vector;
import java.io.*;

/**
 * A Colormap implemented using Catmull-Rom colour splines. The map has a variable number
 * of knots with a minimum of four. The first and last knots give the tangent at the end
 * of the spline, and colours are interpolated from the second to the second-last knots.
 */
public class SplineColormap extends ArrayColormap {

	private int numKnots = 4;
    private int[] xKnots = {
    	0, 0, 255, 255
    };
    private int[] yKnots = {
    	0xff000000, 0xff000000, 0xffffffff, 0xffffffff,
    };
	
	/**
     * Construct a SplineColormap.
     */
    public SplineColormap() {
		rebuildGradient();
	}

	/**
     * Construct a SplineColormap.
     * @param xKnots the knot positions
     * @param yKnots the knot colors
     */
	public SplineColormap(int[] xKnots, int[] yKnots) {
		this.xKnots = xKnots;
		this.yKnots = yKnots;
		numKnots = xKnots.length;
		rebuildGradient();
	}

    /**
     * Set a knot color.
     * @param n the knot index
     * @param color the color
     * @see #getKnot
     */
	public void setKnot(int n, int color) {
		yKnots[n] = color;
		rebuildGradient();
	}
	
    /**
     * Get a knot color.
     * @param n the knot index
     * @return the knot color
     * @see #setKnot
     */
	public int getKnot(int n) {
		return yKnots[n];
	}

    /**
     * Add a new knot.
     * @param x the knot position
     * @param color the color
     * @see #removeKnot
     */
	public void addKnot(int x, int color) {
		int[] nx = new int[numKnots+1];
		int[] ny = new int[numKnots+1];
		System.arraycopy(xKnots, 0, nx, 0, numKnots);
		System.arraycopy(yKnots, 0, ny, 0, numKnots);
		xKnots = nx;
		yKnots = ny;
		xKnots[numKnots] = x;
		yKnots[numKnots] = color;
		numKnots++;
		sortKnots();
		rebuildGradient();
	}
	
    /**
     * Remove a knot.
     * @param n the knot index
     * @see #addKnot
     */
	public void removeKnot(int n) {
		if (numKnots <= 4)
			return;
		if (n < numKnots-1) {
			System.arraycopy(xKnots, n+1, xKnots, n, numKnots-n-1);
			System.arraycopy(yKnots, n+1, yKnots, n, numKnots-n-1);
		}
		numKnots--;
		rebuildGradient();
	}
	
    /**
     * Set a knot position.
     * @param n the knot index
     * @param x the knot position
     */
	public void setKnotPosition(int n, int x) {
		xKnots[n] = PixelUtils.clamp(x);
		sortKnots();
		rebuildGradient();
	}

	private void rebuildGradient() {
		xKnots[0] = -1;
		xKnots[numKnots-1] = 256;
		yKnots[0] = yKnots[1];
		yKnots[numKnots-1] = yKnots[numKnots-2];
		for (int i = 0; i < 256; i++)
			map[i] = ImageMath.colorSpline(i, numKnots, xKnots, yKnots);
	}

	private void sortKnots() {
		for (int i = 1; i < numKnots; i++) {
			for (int j = 1; j < i; j++) {
				if (xKnots[i] < xKnots[j]) {
					int t = xKnots[i];
					xKnots[i] = xKnots[j];
					xKnots[j] = t;
					t = yKnots[i];
					yKnots[i] = yKnots[j];
					yKnots[j] = t;
				}
			}
		}
	}

}
