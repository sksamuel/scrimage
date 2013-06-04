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
 * A Filter to pixellate images.
 */
public class BlockFilter extends AbstractBufferedImageOp {
	
	private int blockSize = 2;

    /**
     * Construct a BlockFilter.
     */
	public BlockFilter() {
	}

    /**
     * Construct a BlockFilter.
	 * @param blockSize the number of pixels along each block edge
     */
	public BlockFilter( int blockSize ) {
		this.blockSize = blockSize;
	}

	/**
	 * Set the pixel block size.
	 * @param blockSize the number of pixels along each block edge
     * @min-value 1
     * @max-value 100+
     * @see #getBlockSize
	 */
	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	/**
	 * Get the pixel block size.
	 * @return the number of pixels along each block edge
     * @see #setBlockSize
	 */
	public int getBlockSize() {
		return blockSize;
	}

    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        int width = src.getWidth();
        int height = src.getHeight();
		int type = src.getType();
		WritableRaster srcRaster = src.getRaster();

        if ( dst == null )
            dst = createCompatibleDestImage( src, null );

		int[] pixels = new int[blockSize * blockSize];
        for ( int y = 0; y < height; y += blockSize ) {
            for ( int x = 0; x < width; x += blockSize ) {
                int w = Math.min( blockSize, width-x );
                int h = Math.min( blockSize, height-y );
                int t = w*h;
                getRGB( src, x, y, w, h, pixels );
                int r = 0, g = 0, b = 0;
                int argb;
                int i = 0;
                for ( int by = 0; by < h; by++ ) {
                    for ( int bx = 0; bx < w; bx++ ) {
                        argb = pixels[i];
                        r += (argb >> 16) & 0xff;
                        g += (argb >> 8) & 0xff;
                        b += argb & 0xff;
                        i++;
                    }
                }
                argb = ((r/t) << 16) | ((g/t) << 8) | (b/t);
                i = 0;
                for ( int by = 0; by < h; by++ ) {
                    for ( int bx = 0; bx < w; bx++ ) {
                        pixels[i] = (pixels[i] & 0xff000000) | argb;
                        i++;
                    }
                }
                setRGB( dst, x, y, w, h, pixels );
            }
        }

        return dst;
    }

	public String toString() {
		return "Pixellate/Mosaic...";
	}
}

