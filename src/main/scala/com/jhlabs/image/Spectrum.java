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

/**
 * A class for calulating the colors of the spectrum.
 */
public class Spectrum {

	private static int adjust(float color, float factor, float gamma) {
		if (color == 0.0)
			return 0;
		return (int)Math.round(255 * Math.pow(color * factor, gamma));
	}
	
	/**
     * Convert a wavelength to an RGB value.
	 * @param wavelength wavelength in nanometres
     * @return the RGB value
	 */
	public static int wavelengthToRGB(float wavelength) {
		float gamma = 0.80f;
		float r, g, b, factor;

		int w = (int)wavelength;
		if (w < 380) {
			r = 0.0f;
			g = 0.0f;
			b = 0.0f;
		} else if (w < 440) {
			r = -(wavelength - 440) / (440 - 380);
			g = 0.0f;
			b = 1.0f;
		} else if (w < 490) {
			r = 0.0f;
			g = (wavelength - 440) / (490 - 440);
			b = 1.0f;
		} else if (w < 510) {
			r = 0.0f;
			g = 1.0f;
			b = -(wavelength - 510) / (510 - 490);
		} else if (w < 580) {
			r = (wavelength - 510) / (580 - 510);
			g = 1.0f;
			b = 0.0f;
		} else if (w < 645) {
			r = 1.0f;
			g = -(wavelength - 645) / (645 - 580);
			b = 0.0f;
		} else if (w <= 780) {
			r = 1.0f;
			g = 0.0f;
			b = 0.0f;
		} else {
			r = 0.0f;
			g = 0.0f;
			b = 0.0f;
		}

		// Let the intensity fall off near the vision limits
		if (380 <= w && w <= 419)
			factor = 0.3f + 0.7f*(wavelength - 380) / (420 - 380);
		else if (420 <= w && w <= 700)
			factor = 1.0f;
		else if (701 <= w && w <= 780)
			factor = 0.3f + 0.7f*(780 - wavelength) / (780 - 700);
		else
			factor = 0.0f;

		int ir = adjust(r, factor, gamma);
		int ig = adjust(g, factor, gamma);
		int ib = adjust(b, factor, gamma);

		return 0xff000000 | (ir << 16) | (ig << 8) | ib;
	}

}
