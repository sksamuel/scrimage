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
import com.jhlabs.math.*;

/**
 * A filter which distorts an image as if it were underwater.
 */
public class SwimFilter extends TransformFilter {

	private float scale = 32;
	private float stretch = 1.0f;
	private float angle = 0.0f;
	private float amount = 1.0f;
	private float turbulence = 1.0f;
	private float time = 0.0f;
	private float m00 = 1.0f;
	private float m01 = 0.0f;
	private float m10 = 0.0f;
	private float m11 = 1.0f;

	public SwimFilter() {
	}
	
	/**
	 * Set the amount of swim.
	 * @param amount the amount of swim
     * @min-value 0
     * @max-value 100+
     * @see #getAmount
	 */
	public void setAmount(float amount) {
		this.amount = amount;
	}
	
	/**
	 * Get the amount of swim.
	 * @return the amount swim
     * @see #setAmount
	 */
	public float getAmount() {
		return amount;
	}
	
	/**
     * Specifies the scale of the distortion.
     * @param scale the scale of the distortion.
     * @min-value 1
     * @max-value 300+
     * @see #getScale
     */
	public void setScale(float scale) {
		this.scale = scale;
	}

	/**
     * Returns the scale of the distortion.
     * @return the scale of the distortion.
     * @see #setScale
     */
	public float getScale() {
		return scale;
	}

	/**
     * Specifies the stretch factor of the distortion.
     * @param stretch the stretch factor of the distortion.
     * @min-value 1
     * @max-value 50+
     * @see #getStretch
     */
	public void setStretch(float stretch) {
		this.stretch = stretch;
	}

	/**
     * Returns the stretch factor of the distortion.
     * @return the stretch factor of the distortion.
     * @see #setStretch
     */
	public float getStretch() {
		return stretch;
	}

	/**
     * Specifies the angle of the effect.
     * @param angle the angle of the effect.
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
     * Returns the angle of the effect.
     * @return the angle of the effect.
     * @see #setAngle
     */
	public float getAngle() {
		return angle;
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
     * Specifies the time. Use this to animate the effect.
     * @param time the time.
     * @angle
     * @see #getTime
     */
	public void setTime(float time) {
		this.time = time;
	}

	/**
     * Returns the time.
     * @return the time.
     * @see #setTime
     */
	public float getTime() {
		return time;
	}

	protected void transformInverse(int x, int y, float[] out) {
		float nx = m00*x + m01*y;
		float ny = m10*x + m11*y;
		nx /= scale;
		ny /= scale * stretch;

		if ( turbulence == 1.0f ) {
			out[0] = x + amount * Noise.noise3(nx+0.5f, ny, time);
			out[1] = y + amount * Noise.noise3(nx, ny+0.5f, time);
		} else {
			out[0] = x + amount * Noise.turbulence3(nx+0.5f, ny, turbulence, time);
			out[1] = y + amount * Noise.turbulence3(nx, ny+0.5f, turbulence, time);
		}
	}

	public String toString() {
		return "Distort/Swim...";
	}
}
