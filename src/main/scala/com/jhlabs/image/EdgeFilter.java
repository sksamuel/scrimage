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
 * An edge-detection filter.
 */
public class EdgeFilter extends WholeImageFilter {
	
	public final static float R2 = (float)Math.sqrt(2);

	public final static float[] ROBERTS_V = {
		0,  0, -1,
		0,  1,  0,
		0,  0,  0,
	};
	public final static float[] ROBERTS_H = {
		-1,  0,  0,
		0,  1,  0,
		0,  0,  0,
	};
	public final static float[] PREWITT_V = {
		-1,  0,  1,
		-1,  0,  1,
		-1,  0,  1,
	};
	public final static float[] PREWITT_H = {
		-1, -1, -1,
		0,  0,  0,
		1,  1,  1,
	};
	public final static float[] SOBEL_V = {
		-1,  0,  1,
		-2,  0,  2,
		-1,  0,  1,
	};
	public static float[] SOBEL_H = {
		-1, -2, -1,
		0,  0,  0,
		1,  2,  1,
	};
	public final static float[] FREI_CHEN_V = {
		-1,  0,  1,
		-R2,  0,  R2,
		-1,  0,  1,
	};
	public static float[] FREI_CHEN_H = {
		-1, -R2, -1,
		0,  0,  0,
		1,  R2,  1,
	};

	protected float[] vEdgeMatrix = SOBEL_V;
	protected float[] hEdgeMatrix = SOBEL_H;

	public EdgeFilter() {
	}

	public void setVEdgeMatrix(float[] vEdgeMatrix) {
		this.vEdgeMatrix = vEdgeMatrix;
	}

	public float[] getVEdgeMatrix() {
		return vEdgeMatrix;
	}

	public void setHEdgeMatrix(float[] hEdgeMatrix) {
		this.hEdgeMatrix = hEdgeMatrix;
	}

	public float[] getHEdgeMatrix() {
		return hEdgeMatrix;
	}

	protected int[] filterPixels( int width, int height, int[] inPixels, Rectangle transformedSpace ) {
		int index = 0;
		int[] outPixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int r = 0, g = 0, b = 0;
				int rh = 0, gh = 0, bh = 0;
				int rv = 0, gv = 0, bv = 0;
				int a = inPixels[y*width+x] & 0xff000000;

				for (int row = -1; row <= 1; row++) {
					int iy = y+row;
					int ioffset;
					if (0 <= iy && iy < height)
						ioffset = iy*width;
					else
						ioffset = y*width;
					int moffset = 3*(row+1)+1;
					for (int col = -1; col <= 1; col++) {
						int ix = x+col;
						if (!(0 <= ix && ix < width))
							ix = x;
						int rgb = inPixels[ioffset+ix];
						float h = hEdgeMatrix[moffset+col];
						float v = vEdgeMatrix[moffset+col];

						r = (rgb & 0xff0000) >> 16;
						g = (rgb & 0x00ff00) >> 8;
						b = rgb & 0x0000ff;
						rh += (int)(h * r);
						gh += (int)(h * g);
						bh += (int)(h * b);
						rv += (int)(v * r);
						gv += (int)(v * g);
						bv += (int)(v * b);
					}
				}
				r = (int)(Math.sqrt(rh*rh + rv*rv) / 1.8);
				g = (int)(Math.sqrt(gh*gh + gv*gv) / 1.8);
				b = (int)(Math.sqrt(bh*bh + bv*bv) / 1.8);
				r = PixelUtils.clamp(r);
				g = PixelUtils.clamp(g);
				b = PixelUtils.clamp(b);
				outPixels[index++] = a | (r << 16) | (g << 8) | b;
			}

		}
		return outPixels;
	}

	public String toString() {
		return "Edges/Detect Edges";
	}
}
