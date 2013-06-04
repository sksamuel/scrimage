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

public class ErodeAlphaFilter extends PointFilter {

	private float threshold;
	private float softness = 0;
    protected float radius = 5;
	private float lowerThreshold;
	private float upperThreshold;

	public ErodeAlphaFilter() {
		this( 3, 0.75f, 0 );
	}

	public ErodeAlphaFilter( float radius, float threshold, float softness ) {
		this.radius = radius;
		this.threshold = threshold;
		this.softness = softness;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}
	
	public float getRadius() {
		return radius;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}
	
	public float getThreshold() {
		return threshold;
	}
	
	public void setSoftness(float softness) {
		this.softness = softness;
	}

	public float getSoftness() {
		return softness;
	}

    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        dst = new GaussianFilter( (int)radius ).filter( src, null );
        lowerThreshold = 255*(threshold - softness*0.5f);
        upperThreshold = 255*(threshold + softness*0.5f);
		return super.filter(dst, dst);
	}

	public int filterRGB(int x, int y, int rgb) {
		int a = (rgb >> 24) & 0xff;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		if ( a == 255 )
            return 0xffffffff;
        float f = ImageMath.smoothStep(lowerThreshold, upperThreshold, (float)a);
        a = (int)(f * 255);
        if ( a < 0 )
            a = 0;
        else if ( a > 255 )
            a = 255;
        return (a << 24) | 0xffffff;
	}

	public String toString() {
		return "Alpha/Erode...";
	}
}
