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

/**
 * A filter which allows channels to be swapped. You provide a matrix with specifying the input channel for 
 * each output channel.
 */
public class SwizzleFilter extends PointFilter {

    private int[] matrix = {
        1, 0, 0, 0, 0,
        0, 1, 0, 0, 0,
        0, 0, 1, 0, 0,
        0, 0, 0, 1, 0
    };
    
	public SwizzleFilter() {
	}

    /**
     * Set the swizzle matrix.
     * @param matrix the matrix
     * @see #getMatrix
     */
	public void setMatrix( int[] matrix ) {
		this.matrix = matrix;
	}

    /**
     * Get the swizzle matrix.
     * @return the matrix
     * @see #setMatrix
     */
	public int[] getMatrix() {
		return matrix;
	}

	public int filterRGB(int x, int y, int rgb) {
		int a = (rgb >> 24) & 0xff;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;

        a = matrix[0]*a + matrix[1]*r + matrix[2]*g + matrix[3]*b + matrix[4]*255;
        r = matrix[5]*a + matrix[6]*r + matrix[7]*g + matrix[8]*b + matrix[9]*255;
        g = matrix[10]*a + matrix[11]*r + matrix[12]*g + matrix[13]*b + matrix[14]*255;
        b = matrix[15]*a + matrix[16]*r + matrix[17]*g + matrix[18]*b + matrix[19]*255;

        a = PixelUtils.clamp( a );
        r = PixelUtils.clamp( r );
        g = PixelUtils.clamp( g );
        b = PixelUtils.clamp( b );

        return (a << 24) | (r << 16) | (g << 8) | b;
	}

	public String toString() {
		return "Channels/Swizzle...";
	}
	
}

