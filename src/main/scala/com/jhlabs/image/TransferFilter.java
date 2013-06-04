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

public abstract class TransferFilter extends PointFilter {

	protected int[] rTable, gTable, bTable;
	protected boolean initialized = false;
	
	public TransferFilter() {
		canFilterIndexColorModel = true;
	}

	public int filterRGB(int x, int y, int rgb) {
		int a = rgb & 0xff000000;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		r = rTable[r];
		g = gTable[g];
		b = bTable[b];
		return a | (r << 16) | (g << 8) | b;
	}

	public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
		if (!initialized)
			initialize();
		return super.filter( src, dst );
	}

	protected void initialize() {
		initialized = true;
		rTable = gTable = bTable = makeTable();
	}

	protected int[] makeTable() {
		int[] table = new int[256];
		for (int i = 0; i < 256; i++)
			table[i] = PixelUtils.clamp( (int)( 255 * transferFunction( i / 255.0f ) ) );
		return table;
	}

	protected float transferFunction( float v ) {
		return 0;
	}

	public int[] getLUT() {
		if (!initialized)
			initialize();
		int[] lut = new int[256];
		for ( int i = 0; i < 256; i++ ) {
			lut[i] = filterRGB( 0, 0, (i << 24) | (i << 16) | (i << 8) | i );
		}
		return lut;
	}
	
}

