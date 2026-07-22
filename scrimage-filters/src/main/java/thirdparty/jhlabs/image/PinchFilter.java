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

package thirdparty.jhlabs.image;

import java.awt.geom.*;
import java.awt.image.*;

/**
 * A filter which performs the popular whirl-and-pinch distortion effect.
 */
public class PinchFilter extends TransformFilter {

	private float angle = 0;
	private float centreX = 0.5f;
	private float centreY = 0.5f;
	private float radius = 100;
	private float amount = 0.5f;

	private float radius2 = 0;
	private float icentreX;
	private float icentreY;
	private float width;
	private float height;

	public PinchFilter() {
	}

    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
		width = src.getWidth();
		height = src.getHeight();
		icentreX = width * centreX;
		icentreY = height * centreY;
		if ( radius == 0 )
			radius = Math.min(icentreX, icentreY);
		radius2 = radius*radius;
		return super.filter( src, dst );
	}

	protected void transformInverse(int x, int y, float[] out) {
		float dx = x-icentreX;
		float dy = y-icentreY;
		float distance = dx*dx + dy*dy;

		if ( distance > radius2 || distance == 0 ) {
			out[0] = x;
			out[1] = y;
		} else {
			float d = (float)Math.sqrt( distance / radius2 );
			float t = (float)Math.pow( Math.sin( Math.PI*0.5 * d ), -amount);

			dx *= t;
			dy *= t;

			float e = 1 - d;
			float a = angle * e * e;

			float s = (float)Math.sin( a );
			float c = (float)Math.cos( a );

			out[0] = icentreX + c*dx - s*dy;
			out[1] = icentreY + s*dx + c*dy;
		}
	}

	public String toString() {
		return "Distort/Pinch...";
	}

}
