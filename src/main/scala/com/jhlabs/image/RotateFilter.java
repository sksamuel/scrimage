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
 * A filter which rotates an image. These days this is easier done with Java2D, but this filter remains.
 */
public class RotateFilter extends TransformFilter {
	
	private float angle;
	private float cos, sin;
	private boolean resize = true;

	/**
     * Construct a RotateFilter.
     */
    public RotateFilter() {
		this(ImageMath.PI);
	}

	/**
     * Construct a RotateFilter.
     * @param angle the angle to rotate
     */
	public RotateFilter(float angle) {
		this(angle, true);
	}

	/**
     * Construct a RotateFilter.
     * @param angle the angle to rotate
     * @param resize true if the output image should be resized
     */
	public RotateFilter(float angle, boolean resize) {
		setAngle(angle);
		this.resize = resize;
	}

	/**
     * Specifies the angle of rotation.
     * @param angle the angle of rotation.
     * @angle
     * @see #getAngle
     */
	public void setAngle(float angle) {
		this.angle = angle;
		cos = (float)Math.cos(this.angle);
		sin = (float)Math.sin(this.angle);
	}

	/**
     * Returns the angle of rotation.
     * @return the angle of rotation.
     * @see #setAngle
     */
	public float getAngle() {
		return angle;
	}

	protected void transformSpace(Rectangle rect) {
		if (resize) {
			Point out = new Point(0, 0);
			int minx = Integer.MAX_VALUE;
			int miny = Integer.MAX_VALUE;
			int maxx = Integer.MIN_VALUE;
			int maxy = Integer.MIN_VALUE;
			int w = rect.width;
			int h = rect.height;
			int x = rect.x;
			int y = rect.y;

			for (int i = 0; i < 4; i++)  {
				switch (i) {
				case 0: transform(x, y, out); break;
				case 1: transform(x + w, y, out); break;
				case 2: transform(x, y + h, out); break;
				case 3: transform(x + w, y + h, out); break;
				}
				minx = Math.min(minx, out.x);
				miny = Math.min(miny, out.y);
				maxx = Math.max(maxx, out.x);
				maxy = Math.max(maxy, out.y);
			}

			rect.x = minx;
			rect.y = miny;
			rect.width = maxx - rect.x;
			rect.height = maxy - rect.y;
		}
	}

	private void transform(int x, int y, Point out) {
		out.x = (int)((x * cos) + (y * sin));
		out.y = (int)((y * cos) - (x * sin));
	}

	protected void transformInverse(int x, int y, float[] out) {
		out[0] = (x * cos) - (y * sin);
		out[1] = (y * cos) + (x * sin);
	}

	public String toString() {
		return "Rotate "+(int)(angle * 180 / Math.PI);
	}

}
