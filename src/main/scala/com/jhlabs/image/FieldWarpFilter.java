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
 * A class which warps an image using a field Warp algorithm.
 */
public class FieldWarpFilter extends TransformFilter {

	public static class Line {
		public int x1, y1, x2, y2;
		public int dx, dy;
		public float length, lengthSquared;
		
		public Line(int x1, int y1, int x2, int y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
		
		public void setup() {
			dx = x2-x1;
			dy = y2-y1;
			lengthSquared = dx*dx + dy*dy;
			length = (float)Math.sqrt(lengthSquared);
		}
	}

	private float amount = 1.0f;
	private float power = 1.0f;
	private float strength = 2.0f;
	private Line[] inLines;
	private Line[] outLines;
	private Line[] intermediateLines;
	private float width, height;

	public FieldWarpFilter() {
	}

	/**
	 * Set the amount of warp.
	 * @param amount the amount
     * @min-value 0
     * @max-value 1
     * @see #getAmount
	 */
	public void setAmount(float amount) {
		this.amount = amount;
	}
	
	/**
	 * Get the amount of warp.
	 * @return the amount
     * @see #setAmount
	 */
	public float getAmount() {
		return amount;
	}
	
	public void setPower(float power) {
		this.power = power;
	}
	
	public float getPower() {
		return power;
	}
	
	public void setStrength(float strength) {
		this.strength = strength;
	}
	
	public float getStrength() {
		return strength;
	}
	
	public void setInLines( Line[] inLines ) {
		this.inLines = inLines;
	}
	
	public Line[] getInLines() {
		return inLines;
	}
	
	public void setOutLines( Line[] outLines ) {
		this.outLines = outLines;
	}
	
	public Line[] getOutLines() {
		return outLines;
	}
	
	protected void transform(int x, int y, Point out) {
	}

	protected void transformInverse(int x, int y, float[] out) {
		float u = 0, v = 0;
		float fraction = 0;
		float distance;
		float fdist;
		float weight;
		float a = 0.001f;
		float b = 1.5f*strength + 0.5f;
		float p = power;

		float totalWeight = 0.0f;
		float sumX = 0.0f;
		float sumY = 0.0f;

		for (int line = 0; line < inLines.length; line++) {
			Line l1 = inLines[line];
			Line l = intermediateLines[line];
			float dx = x - l.x1;
			float dy = y - l.y1;

			fraction = (dx * l.dx + dy * l.dy) / l.lengthSquared;
			fdist = (dy * l.dx - dx * l.dy) / l.length;
			if (fraction <= 0)
				distance = (float)Math.sqrt(dx*dx + dy*dy);
			else if (fraction >= 1) {
				dx = x - l.x2;
				dy = y - l.y2;
				distance = (float)Math.sqrt(dx*dx + dy*dy);
			} else if (fdist >= 0)
				distance = fdist;
			else
				distance = -fdist;
			u = l1.x1 + fraction * l1.dx - fdist * l1.dy / l1.length;
			v = l1.y1 + fraction * l1.dy + fdist * l1.dx / l1.length;

			weight = (float)Math.pow(Math.pow(l.length, p) / (a + distance), b);

			sumX += (u - x) * weight;
			sumY += (v - y) * weight;
//if (x % 10 == 0&&y == 20)System.out.println("distance="+distance+" weight="+weight+" sumX="+sumX+" sumY="+sumY+" u="+u+" v="+v);
			totalWeight += weight;
		}

//		out[0] = ImageMath.clamp(x + sumX / totalWeight + 0.5f, 0, width-1);
//		out[1] = ImageMath.clamp(y + sumY / totalWeight + 0.5f, 0, height-1);
		out[0] = x + sumX / totalWeight + 0.5f;
		out[1] = y + sumY / totalWeight + 0.5f;
	}

    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
		this.width = width;
		this.height = height;
		if ( inLines != null && outLines != null ) {
			intermediateLines = new Line[inLines.length];
			for (int line = 0; line < inLines.length; line++) {
				Line l = intermediateLines[line] = new Line(
					ImageMath.lerp(amount, inLines[line].x1, outLines[line].x1),
					ImageMath.lerp(amount, inLines[line].y1, outLines[line].y1),
					ImageMath.lerp(amount, inLines[line].x2, outLines[line].x2),
					ImageMath.lerp(amount, inLines[line].y2, outLines[line].y2)
				);
				l.setup();
				inLines[line].setup();
			}
			dst = super.filter( src, dst );
			intermediateLines = null;
			return dst;
		}
		return src;
	}

	public String toString() {
		return "Distort/Field Warp...";
	}

}
