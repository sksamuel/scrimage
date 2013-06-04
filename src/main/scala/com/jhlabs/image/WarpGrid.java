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
 * A warp grid.
 * From "A simplified approach to Image Processing" by Randy Crane
 */
public class WarpGrid {

	public float[] xGrid = null;
	public float[] yGrid = null;
	public int rows, cols;

	public WarpGrid(int rows, int cols, int w, int h) {
		this.rows = rows;
		this.cols = cols;
		xGrid = new float[rows*cols];
		yGrid = new float[rows*cols];
		int index = 0;
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				xGrid[index] = (float)col*(w-1)/(cols-1);
				yGrid[index] = (float)row*(h-1)/(rows-1);
				index++;
			}
		}
	}

	/**
	 * Add a new row to the grid. "before" must be in the range 1..rows-1. i.e. you can only add rows inside the grid.
	 */
	public void addRow( int before ) {
		int size = (rows+1) * cols;
		float[] x = new float[size];
		float[] y = new float[size];

		rows++;
		int i = 0;
		int j = 0;
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				int k = j+col;
				int l = i+col;
				if ( row == before ) {
					x[k] = (xGrid[l]+xGrid[k])/2;
					y[k] = (yGrid[l]+yGrid[k])/2;
				} else {
					x[k] = xGrid[l];
					y[k] = yGrid[l];
				}
			}
			if ( row != before-1 )
				i += cols;
			j += cols;
		}
		xGrid = x;
		yGrid = y;
	}

	/**
	 * Add a new column to the grid. "before" must be in the range 1..cols-1. i.e. you can only add columns inside the grid.
	 */
	public void addCol( int before ) {
		int size = rows * (cols+1);
		float[] x = new float[size];
		float[] y = new float[size];

		cols++;
int i = 0;
int j = 0;
		for (int row = 0; row < rows; row++) {
//			int i = row*(cols-1);
//			int j = row*cols;
			for (int col = 0; col < cols; col++) {
				if ( col == before ) {
					x[j] = (xGrid[i]+xGrid[i-1])/2;
					y[j] = (yGrid[i]+yGrid[i-1])/2;
				} else {
					x[j] = xGrid[i];
					y[j] = yGrid[i];
					i++;
				}
				j++;
			}
		}
		xGrid = x;
		yGrid = y;
	}

	/**
	 * Remove a row from the grid.
	 */
	public void removeRow( int r ) {
		int size = (rows-1) * cols;
		float[] x = new float[size];
		float[] y = new float[size];

		rows--;
		int i = 0;
		int j = 0;
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				int k = j+col;
				int l = i+col;
				x[k] = xGrid[l];
				y[k] = yGrid[l];
			}
			if ( row == r-1 )
				i += cols;
			i += cols;
			j += cols;
		}
		xGrid = x;
		yGrid = y;
	}

	/**
	 * Remove a column from the grid.
	 */
	public void removeCol( int r ) {
		int size = rows * (cols+1);
		float[] x = new float[size];
		float[] y = new float[size];

		cols--;
		for (int row = 0; row < rows; row++) {
			int i = row*(cols+1);
			int j = row*cols;
			for (int col = 0; col < cols; col++) {
				x[j] = xGrid[i];
				y[j] = yGrid[i];
				if ( col == r-1 )
					i++;
				i++;
				j++;
			}
		}
		xGrid = x;
		yGrid = y;
	}

	public void lerp(float t, WarpGrid destination, WarpGrid intermediate) {
		if (rows != destination.rows || cols != destination.cols)
			throw new IllegalArgumentException("source and destination are different sizes");
		if (rows != intermediate.rows || cols != intermediate.cols)
			throw new IllegalArgumentException("source and intermediate are different sizes");
		int index = 0;
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				intermediate.xGrid[index] = (float)ImageMath.lerp(t, xGrid[index], destination.xGrid[index]);
				intermediate.yGrid[index] = (float)ImageMath.lerp(t, yGrid[index], destination.yGrid[index]);
				index++;
			}
		}
	}
	
	public void warp(int[] inPixels, int cols, int rows, WarpGrid sourceGrid, WarpGrid destGrid, int[] outPixels) {
try {
		int x, y;
		int u, v;
		int[] intermediate;
		WarpGrid splines;

		if (sourceGrid.rows != destGrid.rows || sourceGrid.cols != destGrid.cols)
			throw new IllegalArgumentException("source and destination grids are different sizes");

		int size = Math.max(cols, rows);
		float[] xrow = new float[size];
		float[] yrow = new float[size];
		float[] scale  = new float[size + 1];
		float[] interpolated = new float[size + 1];

		int gridCols = sourceGrid.cols;
		int gridRows = sourceGrid.rows;

		splines = new WarpGrid(rows, gridCols, 1, 1);

		for (u = 0; u < gridCols;u++) {
			int i = u;

			for (v = 0; v < gridRows;v++) {
				xrow[v] = sourceGrid.xGrid[i];
				yrow[v] = sourceGrid.yGrid[i];
				i += gridCols;
			}

			interpolateSpline(yrow, xrow, 0, gridRows, interpolated, 0, rows);

			i = u;
			for (y = 0;y < rows;y++) {
				splines.xGrid[i] = interpolated[y];
				i += gridCols;
			}
		}

		for (u = 0; u < gridCols;u++) {
			int i = u;

			for (v = 0; v < gridRows;v++) {
				xrow[v] = destGrid.xGrid[i];
				yrow[v] = destGrid.yGrid[i];
				i += gridCols;
			}

			interpolateSpline(yrow, xrow, 0, gridRows, interpolated, 0, rows);

			i = u;
			for (y = 0;y < rows; y++) {
				splines.yGrid[i] = interpolated[y];
				i += gridCols;
			}
		}

		/* first pass: warp x using splines */
		intermediate = new int[rows*cols];

		int offset = 0;
		for (y = 0; y < rows; y++) {
			/* fit spline to x-intercepts;resample over all cols */
			interpolateSpline(splines.xGrid, splines.yGrid, offset, gridCols, scale, 0, cols);
			scale[cols] = cols;
			ImageMath.resample(inPixels, intermediate, cols, y*cols, 1, scale);
			offset += gridCols;
		}
		/* create table of y-intercepts for intermediate mesh's hor splines */

		splines = new WarpGrid(gridRows, cols, 1, 1);

		offset = 0;
		int offset2 = 0;
		for (v = 0; v < gridRows; v++) {
			interpolateSpline(sourceGrid.xGrid, sourceGrid.yGrid, offset, gridCols, splines.xGrid, offset2, cols);
			offset += gridCols;
			offset2 += cols;
		}

		offset = 0;
		offset2 = 0;
		for (v = 0; v < gridRows; v++) {
			interpolateSpline(destGrid.xGrid, destGrid.yGrid, offset, gridCols, splines.yGrid, offset2, cols);
			offset += gridCols;
			offset2 += cols;
		}

		/* second pass: warp y */

		for (x = 0; x < cols; x++) {
			int i = x;
			
			for (v = 0; v < gridRows; v++) {
				xrow[v] = splines.xGrid[i];;
				yrow[v] = splines.yGrid[i];;
				i += cols;
			}

			interpolateSpline(xrow, yrow, 0, gridRows, scale, 0, rows);
			scale[rows] = rows;
			ImageMath.resample(intermediate, outPixels, rows, x, cols, scale);
		}
}
catch (Exception e) {
	e.printStackTrace();
}
	}

	private final static float m00 = -0.5f;
	private final static float m01 =  1.5f;
	private final static float m02 = -1.5f;
	private final static float m03 =  0.5f;
	private final static float m10 =  1.0f;
	private final static float m11 = -2.5f;
	private final static float m12 =  2.0f;
	private final static float m13 = -0.5f;
	private final static float m20 = -0.5f;
	private final static float m22 =  0.5f;
	private final static float m31 =  1.0f;

	protected void interpolateSpline(float[] xKnots, float[] yKnots, int offset, int length, float[] splineY, int splineOffset, int splineLength) {
		int index = offset;
		int end = offset+length-1;
		float x0, x1;
		float k0, k1, k2, k3;
		float c0, c1, c2, c3;

		x0 = xKnots[index];
		k0 = k1 = k2 = yKnots[index];
		x1 = xKnots[index+1];
		k3 = yKnots[index+1];

		for (int i = 0;i < splineLength;i++) {
			if (index <= end && i > xKnots[index]) {
				k0 = k1;
				k1 = k2;
				k2 = k3;
				x0 = xKnots[index];
				index++;
				if ( index <= end )
					x1 = xKnots[index];
				if ( index < end )
					k3 = yKnots[index+1];
				else
					k3 = k2;
			}
			float t = (i - x0) / (x1 - x0);
			c3 = m00*k0 + m01*k1 + m02*k2 + m03*k3;
			c2 = m10*k0 + m11*k1 + m12*k2 + m13*k3;
			c1 = m20*k0 + m22*k2;
			c0 = m31*k1;
			
			splineY[splineOffset+i] = ((c3*t + c2)*t + c1)*t + c0;
		}
	}

	protected void interpolateSpline2(float[] xKnots, float[] yKnots, int offset, float[] splineY, int splineOffset, int splineLength) {
		int index = offset;
		float leftX, rightX;
		float leftY, rightY;

		leftX = xKnots[index];
		leftY = yKnots[index];
		rightX = xKnots[index+1];
		rightY = yKnots[index+1];

		for (int i = 0;i < splineLength;i++) {
			if (i > xKnots[index]) {
				leftX = xKnots[index];
				leftY = yKnots[index];
				index++;
				rightX = xKnots[index];
				rightY = yKnots[index];
			}
			float f = (i - leftX) / (rightX - leftX);
			splineY[splineOffset+i] = leftY + f * (rightY - leftY);
		}
	}
}
