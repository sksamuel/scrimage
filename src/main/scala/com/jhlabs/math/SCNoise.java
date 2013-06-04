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

package com.jhlabs.math;

import java.util.*;

/**
 * Sparse Convolution Noise. This is computationally very expensive, but worth it.
 */
public class SCNoise implements Function1D, Function2D, Function3D {

	private static Random randomGenerator = new Random();
	
	public float evaluate(float x) {
		return evaluate(x, .1f);
	}
	
	public float evaluate(float x, float y) {
		int i, j, k, h, n;
		int ix, iy;
		float sum = 0;
		float fx, fy, dx, dy, distsq;

		if (impulseTab == null)
			impulseTab = impulseTabInit(665);

		ix = floor(x); fx = x - ix;
		iy = floor(y); fy = y - iy;
		
		/* Perform the sparse convolution. */
		int m = 2;
		for (i = -m; i <= m; i++) {
		  for (j = -m; j <= m; j++) {
			/* Compute voxel hash code. */
			h = perm[(ix+i + perm[(iy+j)&TABMASK])&TABMASK];
			
			for (n = NIMPULSES; n > 0; n--, h = (h+1) & TABMASK) {
				/* Convolve filter and impulse. */
				int h4 = h*4;
				dx = fx - (i + impulseTab[h4++]);
				dy = fy - (j + impulseTab[h4++]);
				distsq = dx*dx + dy*dy;
				sum += catrom2(distsq) * impulseTab[h4];
			}
		  }
		}

		return sum / NIMPULSES;
	}
	
	public float evaluate(float x, float y, float z) {
		int i, j, k, h, n;
		int ix, iy, iz;
		float sum = 0;
		float fx, fy, fz, dx, dy, dz, distsq;

		if (impulseTab == null)
			impulseTab = impulseTabInit(665);

		ix = floor(x); fx = x - ix;
		iy = floor(y); fy = y - iy;
		iz = floor(z); fz = z - iz;
		
		/* Perform the sparse convolution. */
		int m = 2;
		for (i = -m; i <= m; i++) {
		  for (j = -m; j <= m; j++) {
			for (k = -m; k <= m; k++) {
				/* Compute voxel hash code. */
				h = perm[(ix+i + perm[(iy+j + perm[(iz+k)&TABMASK])&TABMASK])&TABMASK];
				
				for (n = NIMPULSES; n > 0; n--, h = (h+1) & TABMASK) {
					/* Convolve filter and impulse. */
					int h4 = h*4;
					dx = fx - (i + impulseTab[h4++]);
					dy = fy - (j + impulseTab[h4++]);
					dz = fz - (k + impulseTab[h4++]);
					distsq = dx*dx + dy*dy + dz*dz;
					sum += catrom2(distsq) * impulseTab[h4];
				}
			}
		  }
		}

		return sum / NIMPULSES;
	}
	
	public short[] perm = {
			225,155,210,108,175,199,221,144,203,116, 70,213, 69,158, 33,252,
			  5, 82,173,133,222,139,174, 27,  9, 71, 90,246, 75,130, 91,191,
			169,138,  2,151,194,235, 81,  7, 25,113,228,159,205,253,134,142,
			248, 65,224,217, 22,121,229, 63, 89,103, 96,104,156, 17,201,129,
			 36,  8,165,110,237,117,231, 56,132,211,152, 20,181,111,239,218,
			170,163, 51,172,157, 47, 80,212,176,250, 87, 49, 99,242,136,189,
			162,115, 44, 43,124, 94,150, 16,141,247, 32, 10,198,223,255, 72,
			 53,131, 84, 57,220,197, 58, 50,208, 11,241, 28,  3,192, 62,202,
			 18,215,153, 24, 76, 41, 15,179, 39, 46, 55,  6,128,167, 23,188,
			106, 34,187,140,164, 73,112,182,244,195,227, 13, 35, 77,196,185,
			 26,200,226,119, 31,123,168,125,249, 68,183,230,177,135,160,180,
			 12,  1,243,148,102,166, 38,238,251, 37,240,126, 64, 74,161, 40,
			184,149,171,178,101, 66, 29, 59,146, 61,254,107, 42, 86,154,  4,
			236,232,120, 21,233,209, 45, 98,193,114, 78, 19,206, 14,118,127,
			 48, 79,147, 85, 30,207,219, 54, 88,234,190,122, 95, 67,143,109,
			137,214,145, 93, 92,100,245,  0,216,186, 60, 83,105, 97,204, 52
	};

	private final static int TABSIZE = 256;
	private final static int TABMASK = (TABSIZE-1);
	private final static int NIMPULSES = 3;

	private static float[] impulseTab;

	public static int floor(float x) {
		int ix = (int)x;
		if (x < 0 && x != ix)
			return ix-1;
		return ix;
	}

	private final static int SAMPRATE = 100;  /* table entries per unit distance */
	private final static int NENTRIES = (4*SAMPRATE+1);
	private static float[] table;

	public float catrom2(float d) {
		float x;
		int i;

		if (d >= 4)
			return 0;

		if (table == null) {
 			table = new float[NENTRIES];
 			for (i = 0; i < NENTRIES; i++) {
				x = i/(float)SAMPRATE;
				x = (float)Math.sqrt(x);
				if (x < 1)
					table[i] = 0.5f * (2+x*x*(-5+x*3));
				else
					table[i] = 0.5f * (4+x*(-8+x*(5-x)));
			}
		}

		d = d*SAMPRATE + 0.5f;
		i = floor(d);
		if (i >= NENTRIES)
			return 0;
		return table[i];
	}

	static float[] impulseTabInit(int seed) {
		float[] impulseTab = new float[TABSIZE*4];

		randomGenerator = new Random(seed); /* Set random number generator seed. */
		for (int i = 0; i < TABSIZE; i++) {
			impulseTab[i++] = randomGenerator.nextFloat();
			impulseTab[i++] = randomGenerator.nextFloat();
			impulseTab[i++] = randomGenerator.nextFloat();
			impulseTab[i++] = 1.0f - 2.0f*randomGenerator.nextFloat();
		}
		
		return impulseTab;
	}
	
}
