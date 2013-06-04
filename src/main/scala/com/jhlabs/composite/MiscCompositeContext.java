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

package com.jhlabs.composite;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.color.*;
import java.net.*;
import java.io.*;

public class MiscCompositeContext implements CompositeContext {

	private int rule;
	private float alpha;
	private ColorModel srcColorModel;
	private ColorModel dstColorModel;
	private ColorSpace srcColorSpace;
	private ColorSpace dstColorSpace;
	private boolean srcNeedsConverting;
	private boolean dstNeedsConverting;

	public MiscCompositeContext(int rule,
						 float alpha,
						 ColorModel srcColorModel,
						 ColorModel dstColorModel) {
		this.rule = rule;
		this.alpha = alpha;
		this.srcColorModel = srcColorModel;
		this.dstColorModel = dstColorModel;
		this.srcColorSpace = srcColorModel.getColorSpace();
		this.dstColorSpace = dstColorModel.getColorSpace();
		ColorModel srgbCM = ColorModel.getRGBdefault();
//		srcNeedsConverting = !srcColorModel.equals(srgbCM);
//		dstNeedsConverting = !dstColorModel.equals(srgbCM);
	}

	public void dispose() {
	}
	
	// Multiply two numbers in the range 0..255 such that 255*255=255
	static int multiply255( int a, int b ) {
		int t = a * b + 0x80;
		return ((t >> 8) + t) >> 8;
	}
	
	static int clamp( int a ) {
		return a < 0 ? 0 : a > 255 ? 255 : a;
	}
	
	public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
		float a=0, ac=0;
		float alpha = this.alpha;
		int t;

		float[] sHsv = null, diHsv = null, doHsv = null;
		switch ( rule ) {
		case MiscComposite.HUE:
		case MiscComposite.SATURATION:
		case MiscComposite.VALUE:
		case MiscComposite.COLOR: 
			sHsv = new float[3];
			diHsv = new float[3];
			doHsv = new float[3];
			break;
		}

		int[] srcPix = null;
		int[] dstPix = null;

		int x = dstOut.getMinX();
		int w = dstOut.getWidth();

		int y0 = dstOut.getMinY();
		int y1 = y0 + dstOut.getHeight();

		for ( int y = y0; y < y1; y++ ) {
			srcPix = src.getPixels(x, y, w, 1, srcPix);
			dstPix = dstIn.getPixels(x, y, w, 1, dstPix);
			int i = 0;
			int end = w*4;

			while ( i < end ) {
				int sr = srcPix[i];
				int dir = dstPix[i];
				int sg = srcPix[i+1];
				int dig = dstPix[i+1];
				int sb = srcPix[i+2];
				int dib = dstPix[i+2];
				int sa = srcPix[i+3];
				int dia = dstPix[i+3];
				int dor, dog, dob, doa;

				switch ( rule ) {
				case MiscComposite.ADD:
				default:
					dor = dir + sr;
					if ( dor > 255 )
						dor = 255;
					dog = dig + sg;
					if ( dog > 255 )
						dog = 255;
					dob = dib + sb;
					if ( dob > 255 )
						dob = 255;
					break;

				case MiscComposite.SUBTRACT:
					dor = dir - sr;
					if ( dor < 0 )
						dor = 0;
					dog = dig - sg;
					if ( dog < 0 )
						dog = 0;
					dob = dib - sb;
					if ( dob < 0 )
						dob = 0;
					break;

				case MiscComposite.DIFFERENCE:
					dor = dir - sr;
					if ( dor < 0 )
						dor = -dor;
					dog = dig - sg;
					if ( dog < 0 )
						dog = -dog;
					dob = dib - sb;
					if ( dob < 0 )
						dob = -dob;
					break;

				case MiscComposite.MULTIPLY:
					t = dir * sr + 0x80;
					dor = ((t >> 8) + t) >> 8;
					t = dig * sg + 0x80;
					dog = ((t >> 8) + t) >> 8;
					t = dib * sb + 0x80;
					dob = ((t >> 8) + t) >> 8;
					break;

				case MiscComposite.SCREEN:
					t = (255-dir) * (255-sr) + 0x80;
					dor = 255 - ( ((t >> 8) + t) >> 8 );
					t = (255-dig) * (255-sg) + 0x80;
					dog = 255 - ( ((t >> 8) + t) >> 8 );
					t = (255-dib) * (255-sb) + 0x80;
					dob = 255 - ( ((t >> 8) + t) >> 8 );
					break;

				case MiscComposite.OVERLAY:
					if ( dir < 128 ) {
						t = dir * sr + 0x80;
						dor = 2 * (((t >> 8) + t) >> 8);
					} else {
						t = (255-dir) * (255-sr) + 0x80;
						dor = 2 * (255 - ( ((t >> 8) + t) >> 8 ));
					}
					if ( dig < 128 ) {
						t = dig * sg + 0x80;
						dog = 2 * (((t >> 8) + t) >> 8);
					} else {
						t = (255-dig) * (255-sg) + 0x80;
						dog = 2 * (255 - ( ((t >> 8) + t) >> 8 ));
					}
					if ( dib < 128 ) {
						t = dib * sb + 0x80;
						dob = 2 * (((t >> 8) + t) >> 8);
					} else {
						t = (255-dib) * (255-sb) + 0x80;
						dob = 2 * (255 - ( ((t >> 8) + t) >> 8 ));
					}
					break;

				case MiscComposite.DARKEN:
					dor = dir < sr ? dir : sr;
					dog = dig < sg ? dig : sg;
					dob = dib < sb ? dib : sb;
					break;

				case MiscComposite.LIGHTEN:
					dor = dir > sr ? dir : sr;
					dog = dig > sg ? dig : sg;
					dob = dib > sb ? dib : sb;
					break;

				case MiscComposite.AVERAGE:
					dor = (dir + sr) / 2;
					dog = (dig + sg) / 2;
					dob = (dib + sb) / 2;
					break;

				case MiscComposite.HUE:
				case MiscComposite.SATURATION:
				case MiscComposite.VALUE:
				case MiscComposite.COLOR:
					Color.RGBtoHSB(sr, sg, sb, sHsv);
					Color.RGBtoHSB(dir, dig, dib, diHsv);

					switch(rule) {
					case MiscComposite.HUE:
						doHsv[0] = sHsv[0];
						doHsv[1] = diHsv[1];
						doHsv[2] = diHsv[2];
						break;
					case MiscComposite.SATURATION:
						doHsv[0] = diHsv[0];
						doHsv[1] = sHsv[1];
						doHsv[2] = diHsv[2];
						break;
					case MiscComposite.VALUE:
						doHsv[0] = diHsv[0];
						doHsv[1] = diHsv[1];
						doHsv[2] = sHsv[2];
						break;
					case MiscComposite.COLOR: 
						doHsv[0] = sHsv[0];
						doHsv[1] = sHsv[1];
						doHsv[2] = diHsv[2];
						break;
					}

					int doRGB = Color.HSBtoRGB(doHsv[0], doHsv[1], doHsv[2]);
					dor = (doRGB&0xff0000)>>16;
					dog = (doRGB&0xff00)>>8;
					dob = (doRGB&0xff);
					break;

				case MiscComposite.BURN:
					if (dir != 255)
						dor = clamp(255-(((int)(255-sr) << 8) / (dir+1)));
					else
						dor = sr;
					if (dig != 255)
						dog = clamp(255-(((int)(255-sg) << 8) / (dig+1)));
					else
						dog = sg;
					if (dib != 255)
						dob = clamp(255-(((int)(255-sb) << 8) / (dib+1)));
					else
						dob = sb;
					break;

				case MiscComposite.COLOR_BURN:
					if (sr != 0)
						dor = Math.max(255 - (((int)(255-dir) << 8) / sr), 0);
					else
						dor = sr;
					if (sg != 0)
						dog = Math.max(255 - (((int)(255-dig) << 8) / sg), 0);
					else
						dog = sg;
					if (sb != 0)
						dob = Math.max(255 - (((int)(255-dib) << 8) / sb), 0);
					else
						dob = sb;
					break;

				case MiscComposite.DODGE:
					dor = clamp((sr << 8) / (256-dir));
					dog = clamp((sg << 8) / (256-dig));
					dob = clamp((sb << 8) / (256-dib));
					break;

				case MiscComposite.COLOR_DODGE:
					if (sr != 255)
						dor = Math.min((dir << 8) / (255-sr), 255);
					else
						dor = sr;
					if (sg != 255)
						dog = Math.min((dig << 8) / (255-sg), 255);
					else
						dog = sg;
					if (sb != 255)
						dob = Math.min((dib << 8) / (255-sb), 255);
					else
						dob = sb;
					break;

				case MiscComposite.SOFT_LIGHT:
					int d;
					d = multiply255(sr, dir);
					dor = d + multiply255(dir, 255 - multiply255(255-dir, 255-sr)-d);
					d = multiply255(sg, dig);
					dog = d + multiply255(dig, 255 - multiply255(255-dig, 255-sg)-d);
					d = multiply255(sb, dib);
					dob = d + multiply255(dib, 255 - multiply255(255-dib, 255-sb)-d);
					break;

				case MiscComposite.HARD_LIGHT:
					if (sr > 127)
						dor = 255 - 2*multiply255(255 - sr, 255 - dir);
					else
						dor = 2*multiply255(sr, dir);
					if (sg > 127)
						dog = 255 - 2*multiply255(255 - sg, 255 - dig);
					else
						dog = 2*multiply255(sg, dig);
					if (sb > 127)
						dob = 255 - 2*multiply255(255 - sb, 255 - dib);
					else
						dob = 2*multiply255(sb, dib);
					break;

				case MiscComposite.PIN_LIGHT:
					dor = sr > 127 ? Math.max(sr, dir) : Math.min(sr, dir);
					dog = sg > 127 ? Math.max(sg, dig) : Math.min(sg, dig);
					dob = sb > 127 ? Math.max(sb, dib) : Math.min(sb, dib);
					break;

				case MiscComposite.EXCLUSION:
					dor = dir+multiply255(sr, (255-dir-dir));
					dog = dig+multiply255(sg, (255-dig-dig));
					dob = dib+multiply255(sb, (255-dib-dib));
					break;

				case MiscComposite.NEGATION:
					dor = 255 - Math.abs(255-sr-dir);
					dog = 255 - Math.abs(255-sg-dig);
					dob = 255 - Math.abs(255-sb-dib);
					break;
				}

				a = alpha*sa/255f;
				ac = 1-a;

				dstPix[i] = (int)(a*dor + ac*dir);
				dstPix[i+1] = (int)(a*dog + ac*dig);
				dstPix[i+2] = (int)(a*dob + ac*dib);
				dstPix[i+3] = (int)(sa*alpha + dia*ac);
				i += 4;
			}
			dstOut.setPixels(x, y, w, 1, dstPix);
		}
	}

}
