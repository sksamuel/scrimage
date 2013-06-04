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
import java.awt.event.*;
import java.awt.image.*;

/**
 * A filter for warping images using the gridwarp algorithm.
 * You need to supply two warp grids, one for the source image and
 * one for the destination image. The image will be warped so that
 * a point in the source grid moves to its counterpart in the destination
 * grid.
 */
public class WarpFilter extends WholeImageFilter {

	private WarpGrid sourceGrid;
	private WarpGrid destGrid;
	private int frames = 1;

	private BufferedImage morphImage;
	private float time;

	/**
	 * Create a WarpFilter.
	 */
	public WarpFilter() {
	}
	
	/**
	 * Create a WarpFilter with two warp grids.
	 * @param sourceGrid the source grid
	 * @param destGrid the destination grid
	 */
	public WarpFilter(WarpGrid sourceGrid, WarpGrid destGrid) {
		this.sourceGrid = sourceGrid;
		this.destGrid = destGrid;		
	}
	
	/**
	 * Set the source warp grid.
	 * @param sourceGrid the source grid
     * @see #getSourceGrid
	 */
	public void setSourceGrid(WarpGrid sourceGrid) {
		this.sourceGrid = sourceGrid;
	}

	/**
	 * Get the source warp grid.
	 * @return the source grid
     * @see #setSourceGrid
	 */
	public WarpGrid getSourceGrid() {
		return sourceGrid;
	}

	/**
	 * Set the destination warp grid.
	 * @param destGrid the destination grid
     * @see #getDestGrid
	 */
	public void setDestGrid(WarpGrid destGrid) {
		this.destGrid = destGrid;
	}

	/**
	 * Get the destination warp grid.
	 * @return the destination grid
     * @see #setDestGrid
	 */
	public WarpGrid getDestGrid() {
		return destGrid;
	}

	public void setFrames(int frames) {
		this.frames = frames;
	}

	public int getFrames() {
		return frames;
	}

	/**
	 * For morphing, sets the image we're morphing to. If not, set then we're just warping.
	 */
	public void setMorphImage(BufferedImage morphImage) {
		this.morphImage = morphImage;
	}

	public BufferedImage getMorphImage() {
		return morphImage;
	}

	public void setTime(float time) {
		this.time = time;
	}

	public float getTime() {
		return time;
	}

	protected void transformSpace(Rectangle r) {
		r.width *= frames;
	}

	protected int[] filterPixels( int width, int height, int[] inPixels, Rectangle transformedSpace ) {
		int[] outPixels = new int[width * height];
		
		if ( morphImage != null ) {
			int[] morphPixels = getRGB( morphImage, 0, 0, width, height, null );
			morph( inPixels, morphPixels, outPixels, sourceGrid, destGrid, width, height, time );
		} else if (frames <= 1) {
			sourceGrid.warp(inPixels, width, height, sourceGrid, destGrid, outPixels);
		} else {
			WarpGrid newGrid = new WarpGrid(sourceGrid.rows, sourceGrid.cols, width, height);
			for (int i = 0; i < frames; i++) {
				float t = (float)i/(frames-1);
				sourceGrid.lerp(t, destGrid, newGrid);
				sourceGrid.warp(inPixels, width, height, sourceGrid, newGrid, outPixels);
			}
		}
		return outPixels;
	}

	public void morph(int[] srcPixels, int[] destPixels, int[] outPixels, WarpGrid srcGrid, WarpGrid destGrid, int width, int height, float t) {
		WarpGrid newGrid = new WarpGrid(srcGrid.rows, srcGrid.cols, width, height);
		srcGrid.lerp(t, destGrid, newGrid);
		srcGrid.warp(srcPixels, width, height, srcGrid, newGrid, outPixels);
		int[] destPixels2 = new int[width * height];
		destGrid.warp(destPixels, width, height, destGrid, newGrid, destPixels2);
		crossDissolve(outPixels, destPixels2, width, height, t);
	}

	public void crossDissolve(int[] pixels1, int[] pixels2, int width, int height, float t) {
		int index = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pixels1[index] = ImageMath.mixColors(t, pixels1[index], pixels2[index]);
				index++;
			}
		}
	}
	
	public String toString() {
		return "Distort/Mesh Warp...";
	}

}

