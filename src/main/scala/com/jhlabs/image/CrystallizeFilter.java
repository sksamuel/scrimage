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
import java.util.*;
import com.jhlabs.math.*;

/**
 * A filter which applies a crystallizing effect to an image, by producing Voronoi cells filled with colours from the image.
 */
public class CrystallizeFilter extends CellularFilter {

	private float edgeThickness = 0.4f;
	private boolean fadeEdges = false;
	private int edgeColor = 0xff000000;

	public CrystallizeFilter() {
		setScale(16);
		setRandomness(0.0f);
	}
	
	public void setEdgeThickness(float edgeThickness) {
		this.edgeThickness = edgeThickness;
	}

	public float getEdgeThickness() {
		return edgeThickness;
	}

	public void setFadeEdges(boolean fadeEdges) {
		this.fadeEdges = fadeEdges;
	}

	public boolean getFadeEdges() {
		return fadeEdges;
	}

	public void setEdgeColor(int edgeColor) {
		this.edgeColor = edgeColor;
	}

	public int getEdgeColor() {
		return edgeColor;
	}

	public int getPixel(int x, int y, int[] inPixels, int width, int height) {
		float nx = m00*x + m01*y;
		float ny = m10*x + m11*y;
		nx /= scale;
		ny /= scale * stretch;
		nx += 1000;
		ny += 1000;	// Reduce artifacts around 0,0
		float f = evaluate(nx, ny);

		float f1 = results[0].distance;
		float f2 = results[1].distance;
		int srcx = ImageMath.clamp((int)((results[0].x-1000)*scale), 0, width-1);
		int srcy = ImageMath.clamp((int)((results[0].y-1000)*scale), 0, height-1);
		int v = inPixels[srcy * width + srcx];
		f = (f2 - f1) / edgeThickness;
		f = ImageMath.smoothStep(0, edgeThickness, f);
		if (fadeEdges) {
			srcx = ImageMath.clamp((int)((results[1].x-1000)*scale), 0, width-1);
			srcy = ImageMath.clamp((int)((results[1].y-1000)*scale), 0, height-1);
			int v2 = inPixels[srcy * width + srcx];
			v2 = ImageMath.mixColors(0.5f, v2, v);
			v = ImageMath.mixColors(f, v2, v);
		} else
			v = ImageMath.mixColors(f, edgeColor, v);
		return v;
	}

	public String toString() {
		return "Pixellate/Crystallize...";
	}
	
}
