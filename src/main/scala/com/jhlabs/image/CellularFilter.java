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
import com.jhlabs.math.*;

/**
 * A filter which produces an image with a cellular texture.
 */
public class CellularFilter extends WholeImageFilter implements Function2D, Cloneable {

	protected float scale = 32;
	protected float stretch = 1.0f;
	protected float angle = 0.0f;
	public float amount = 1.0f;
	public float turbulence = 1.0f;
	public float gain = 0.5f;
	public float bias = 0.5f;
	public float distancePower = 2;
	public boolean useColor = false;
	protected Colormap colormap = new Gradient();
	protected float[] coefficients = { 1, 0, 0, 0 };
	protected float angleCoefficient;
	protected Random random = new Random();
	protected float m00 = 1.0f;
	protected float m01 = 0.0f;
	protected float m10 = 0.0f;
	protected float m11 = 1.0f;
	protected Point[] results = null;
	protected float randomness = 0;
	protected int gridType = HEXAGONAL;
	private float min;
	private float max;
	private static byte[] probabilities;
	private float gradientCoefficient;
	
	public final static int RANDOM = 0;
	public final static int SQUARE = 1;
	public final static int HEXAGONAL = 2;
	public final static int OCTAGONAL = 3;
	public final static int TRIANGULAR = 4;

	public CellularFilter() {
		results = new Point[3];
		for (int j = 0; j < results.length; j++)
			results[j] = new Point();
		if (probabilities == null) {
			probabilities = new byte[8192];
			float factorial = 1;
			float total = 0;
			float mean = 2.5f;
			for (int i = 0; i < 10; i++) {
				if (i > 1)
					factorial *= i;
				float probability = (float)Math.pow(mean, i) * (float)Math.exp(-mean) / factorial;
				int start = (int)(total * 8192);
				total += probability;
				int end = (int)(total * 8192);
				for (int j = start; j < end; j++)
					probabilities[j] = (byte)i;
			}	
		}
	}
	
	/**
     * Specifies the scale of the texture.
     * @param scale the scale of the texture.
     * @min-value 1
     * @max-value 300+
     * @see #getScale
     */
	public void setScale(float scale) {
		this.scale = scale;
	}

	/**
     * Returns the scale of the texture.
     * @return the scale of the texture.
     * @see #setScale
     */
	public float getScale() {
		return scale;
	}

	/**
     * Specifies the stretch factor of the texture.
     * @param stretch the stretch factor of the texture.
     * @min-value 1
     * @max-value 50+
     * @see #getStretch
     */
	public void setStretch(float stretch) {
		this.stretch = stretch;
	}

	/**
     * Returns the stretch factor of the texture.
     * @return the stretch factor of the texture.
     * @see #setStretch
     */
	public float getStretch() {
		return stretch;
	}

	/**
     * Specifies the angle of the texture.
     * @param angle the angle of the texture.
     * @angle
     * @see #getAngle
     */
	public void setAngle(float angle) {
		this.angle = angle;
		float cos = (float)Math.cos(angle);
		float sin = (float)Math.sin(angle);
		m00 = cos;
		m01 = sin;
		m10 = -sin;
		m11 = cos;
	}

	/**
     * Returns the angle of the texture.
     * @return the angle of the texture.
     * @see #setAngle
     */
	public float getAngle() {
		return angle;
	}

	public void setCoefficient(int i, float v) {
		coefficients[i] = v;
	}

	public float getCoefficient(int i) {
		return coefficients[i];
	}

	public void setAngleCoefficient(float angleCoefficient) {
		this.angleCoefficient = angleCoefficient;
	}

	public float getAngleCoefficient() {
		return angleCoefficient;
	}

	public void setGradientCoefficient(float gradientCoefficient) {
		this.gradientCoefficient = gradientCoefficient;
	}

	public float getGradientCoefficient() {
		return gradientCoefficient;
	}

	public void setF1( float v ) {
		coefficients[0] = v;
	}

	public float getF1() {
		return coefficients[0];
	}

	public void setF2( float v ) {
		coefficients[1] = v;
	}

	public float getF2() {
		return coefficients[1];
	}

	public void setF3( float v ) {
		coefficients[2] = v;
	}

	public float getF3() {
		return coefficients[2];
	}

	public void setF4( float v ) {
		coefficients[3] = v;
	}

	public float getF4() {
		return coefficients[3];
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
	
	public void setRandomness(float randomness) {
		this.randomness = randomness;
	}

	public float getRandomness() {
		return randomness;
	}

	public void setGridType(int gridType) {
		this.gridType = gridType;
	}

	public int getGridType() {
		return gridType;
	}

	public void setDistancePower(float distancePower) {
		this.distancePower = distancePower;
	}

	public float getDistancePower() {
		return distancePower;
	}

	/**
     * Specifies the turbulence of the texture.
     * @param turbulence the turbulence of the texture.
     * @min-value 0
     * @max-value 1
     * @see #getTurbulence
     */
	public void setTurbulence(float turbulence) {
		this.turbulence = turbulence;
	}

	/**
     * Returns the turbulence of the effect.
     * @return the turbulence of the effect.
     * @see #setTurbulence
     */
	public float getTurbulence() {
		return turbulence;
	}

	/**
	 * Set the amount of effect.
	 * @param amount the amount
     * @min-value 0
     * @max-value 1
     * @see #getAmount
	 */
	public void setAmount(float amount) {
		this.amount = amount;
	}

	/**
	 * Get the amount of texture.
	 * @return the amount
     * @see #setAmount
	 */
	public float getAmount() {
		return amount;
	}

	public class Point {
		public int index;
		public float x, y;
		public float dx, dy;
		public float cubeX, cubeY;
		public float distance;
	}
	
	private float checkCube(float x, float y, int cubeX, int cubeY, Point[] results) {
		int numPoints;
		random.setSeed(571*cubeX + 23*cubeY);
		switch (gridType) {
		case RANDOM:
		default:
			numPoints = probabilities[random.nextInt() & 0x1fff];
			break;
		case SQUARE:
			numPoints = 1;
			break;
		case HEXAGONAL:
			numPoints = 1;
			break;
		case OCTAGONAL:
			numPoints = 2;
			break;
		case TRIANGULAR:
			numPoints = 2;
			break;
		}
		for (int i = 0; i < numPoints; i++) {
			float px = 0, py = 0;
			float weight = 1.0f;
			switch (gridType) {
			case RANDOM:
				px = random.nextFloat();
				py = random.nextFloat();
				break;
			case SQUARE:
				px = py = 0.5f;
				if (randomness != 0) {
					px += randomness * (random.nextFloat()-0.5);
					py += randomness * (random.nextFloat()-0.5);
				}
				break;
			case HEXAGONAL:
				if ((cubeX & 1) == 0) {
					px = 0.75f; py = 0;
				} else {
					px = 0.75f; py = 0.5f;
				}
				if (randomness != 0) {
					px += randomness * Noise.noise2(271*(cubeX+px), 271*(cubeY+py));
					py += randomness * Noise.noise2(271*(cubeX+px)+89, 271*(cubeY+py)+137);
				}
				break;
			case OCTAGONAL:
				switch (i) {
				case 0: px = 0.207f; py = 0.207f; break;
				case 1: px = 0.707f; py = 0.707f; weight = 1.6f; break;
				}
				if (randomness != 0) {
					px += randomness * Noise.noise2(271*(cubeX+px), 271*(cubeY+py));
					py += randomness * Noise.noise2(271*(cubeX+px)+89, 271*(cubeY+py)+137);
				}
				break;
			case TRIANGULAR:
				if ((cubeY & 1) == 0) {
					if (i == 0) {
						px = 0.25f; py = 0.35f;
					} else {
						px = 0.75f; py = 0.65f;
					}
				} else {
					if (i == 0) {
						px = 0.75f; py = 0.35f;
					} else {
						px = 0.25f; py = 0.65f;
					}
				}
				if (randomness != 0) {
					px += randomness * Noise.noise2(271*(cubeX+px), 271*(cubeY+py));
					py += randomness * Noise.noise2(271*(cubeX+px)+89, 271*(cubeY+py)+137);
				}
				break;
			}
			float dx = (float)Math.abs(x-px);
			float dy = (float)Math.abs(y-py);
			float d;
			dx *= weight;
			dy *= weight;
			if (distancePower == 1.0f)
				d = dx + dy;
			else if (distancePower == 2.0f)
				d = (float)Math.sqrt(dx*dx + dy*dy);
			else
				d = (float)Math.pow((float)Math.pow(dx, distancePower) + (float)Math.pow(dy, distancePower), 1/distancePower);

			// Insertion sort the long way round to speed it up a bit
			if (d < results[0].distance) {
				Point p = results[2];
				results[2] = results[1];
				results[1] = results[0];
				results[0] = p;
				p.distance = d;
				p.dx = dx;
				p.dy = dy;
				p.x = cubeX+px;
				p.y = cubeY+py;
			} else if (d < results[1].distance) {
				Point p = results[2];
				results[2] = results[1];
				results[1] = p;
				p.distance = d;
				p.dx = dx;
				p.dy = dy;
				p.x = cubeX+px;
				p.y = cubeY+py;
			} else if (d < results[2].distance) {
				Point p = results[2];
				p.distance = d;
				p.dx = dx;
				p.dy = dy;
				p.x = cubeX+px;
				p.y = cubeY+py;
			}
		}
		return results[2].distance;
	}
	
	public float evaluate(float x, float y) {
		for (int j = 0; j < results.length; j++)
			results[j].distance = Float.POSITIVE_INFINITY;

		int ix = (int)x;
		int iy = (int)y;
		float fx = x-ix;
		float fy = y-iy;

		float d = checkCube(fx, fy, ix, iy, results);
		if (d > fy)
			d = checkCube(fx, fy+1, ix, iy-1, results);
		if (d > 1-fy)
			d = checkCube(fx, fy-1, ix, iy+1, results);
		if (d > fx) {
			checkCube(fx+1, fy, ix-1, iy, results);
			if (d > fy)
				d = checkCube(fx+1, fy+1, ix-1, iy-1, results);
			if (d > 1-fy)
				d = checkCube(fx+1, fy-1, ix-1, iy+1, results);
		}
		if (d > 1-fx) {
			d = checkCube(fx-1, fy, ix+1, iy, results);
			if (d > fy)
				d = checkCube(fx-1, fy+1, ix+1, iy-1, results);
			if (d > 1-fy)
				d = checkCube(fx-1, fy-1, ix+1, iy+1, results);
		}

		float t = 0;
		for (int i = 0; i < 3; i++)
			t += coefficients[i] * results[i].distance;
		if (angleCoefficient != 0) {
			float angle = (float)Math.atan2(y-results[0].y, x-results[0].x);
			if (angle < 0)
				angle += 2*(float)Math.PI;
			angle /= 4*(float)Math.PI;
			t += angleCoefficient * angle;
		}
		if (gradientCoefficient != 0) {
			float a = 1/(results[0].dy+results[0].dx);
			t += gradientCoefficient * a;
		}
		return t;
	}
	
	public float turbulence2(float x, float y, float freq) {
		float t = 0.0f;

		for (float f = 1.0f; f <= freq; f *= 2)
			t += evaluate(f*x, f*y) / f;
		return t;
	}

	public int getPixel(int x, int y, int[] inPixels, int width, int height) {
		float nx = m00*x + m01*y;
		float ny = m10*x + m11*y;
		nx /= scale;
		ny /= scale * stretch;
		nx += 1000;
		ny += 1000;	// Reduce artifacts around 0,0
		float f = turbulence == 1.0f ? evaluate(nx, ny) : turbulence2(nx, ny, turbulence);
		// Normalize to 0..1
//		f = (f-min)/(max-min);
		f *= 2;
		f *= amount;
		int a = 0xff000000;
		int v;
		if (colormap != null) {
			v = colormap.getColor(f);
			if (useColor) {
				int srcx = ImageMath.clamp((int)((results[0].x-1000)*scale), 0, width-1);
				int srcy = ImageMath.clamp((int)((results[0].y-1000)*scale), 0, height-1);
				v = inPixels[srcy * width + srcx];
				f = (results[1].distance - results[0].distance) / (results[1].distance + results[0].distance);
				f = ImageMath.smoothStep(coefficients[1], coefficients[0], f);
				v = ImageMath.mixColors(f, 0xff000000, v);
			}
			return v;
		} else {
			v = PixelUtils.clamp((int)(f*255));
			int r = v << 16;
			int g = v << 8;
			int b = v;
			return a|r|g|b;
		}
	}

	protected int[] filterPixels( int width, int height, int[] inPixels, Rectangle transformedSpace ) {
//		float[] minmax = Noise.findRange(this, null);
//		min = minmax[0];
//		max = minmax[1];

		int index = 0;
		int[] outPixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				outPixels[index++] = getPixel(x, y, inPixels, width, height);
			}
		}
		return outPixels;
	}

	public Object clone() {
		CellularFilter f = (CellularFilter)super.clone();
		f.coefficients = (float[])coefficients.clone();
		f.results = (Point[])results.clone();
		f.random = new Random();
//		if (colormap != null)
//			f.colormap = (Colormap)colormap.clone();
		return f;
	}
	
	public String toString() {
		return "Texture/Cellular...";
	}
	
}
