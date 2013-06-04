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
 * A BufferedImageOp which iterates another BufferedImageOp.
 */
public class IteratedFilter extends AbstractBufferedImageOp {
	private BufferedImageOp filter;
	private int iterations;
	
    /**
     * Construct an IteratedFilter.
     * @param filter the filetr to iterate
     * @param iterations the number of iterations
     */
	public IteratedFilter( BufferedImageOp filter, int iterations ) {
		this.filter = filter;
		this.iterations = iterations;
	}
	
	public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
		BufferedImage image = src;

		for ( int i = 0; i < iterations; i++ )
			image = filter.filter( image, dst );
		
		return image;
	}
}
