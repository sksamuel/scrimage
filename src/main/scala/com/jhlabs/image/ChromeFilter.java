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

import java.awt.image.*;

/**
 * A filter which simulates chrome.
 */
public class ChromeFilter extends LightFilter {
	private float amount = 0.5f;
	private float exposure = 1.0f;

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
	 * Get the amount of chrome.
	 * @return the amount
     * @see #setAmount
	 */
	public float getAmount() {
		return amount;
	}

	/**
	 * Set the exppsure of the effect.
	 * @param exposure the exposure
     * @min-value 0
     * @max-value 1
     * @see #getExposure
	 */
	public void setExposure(float exposure) {
		this.exposure = exposure;
	}
	
	/**
	 * Get the exppsure of the effect.
	 * @return the exposure
     * @see #setExposure
	 */
	public float getExposure() {
		return exposure;
	}

    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
		setColorSource( LightFilter.COLORS_CONSTANT );
		dst = super.filter( src, dst );
		TransferFilter tf = new TransferFilter() {
			protected float transferFunction( float v ) {
				v += amount * (float)Math.sin( v * 2 * Math.PI );
				return 1 - (float)Math.exp(-v * exposure);
			}
		};
        return tf.filter( dst, dst );
    }

	public String toString() {
		return "Effects/Chrome...";
	}
}

