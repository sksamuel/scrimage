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
 * A filter which produces  the stipple effect for Swing icons specified in the Java Look and Feel Guidelines.
 */
public class JavaLnFFilter extends PointFilter {

	public JavaLnFFilter() {
	}

	public int filterRGB(int x, int y, int rgb) {
		if ((x & 1) == (y & 1))
			return rgb;
		return ImageMath.mixColors(0.25f, 0xff999999, rgb);
	}

	public String toString() {
		return "Stylize/Java L&F Stipple";
	}

}


