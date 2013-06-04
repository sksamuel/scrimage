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

public class FBM implements Function2D {

	protected float[] exponents;
	protected float H;
	protected float lacunarity;
	protected float octaves;
	protected Function2D basis;

	public FBM(float H, float lacunarity, float octaves) {
		this(H, lacunarity, octaves, new Noise());
	}
	
	public FBM(float H, float lacunarity, float octaves, Function2D basis) {
		this.H = H;
		this.lacunarity = lacunarity;
		this.octaves = octaves;
		this.basis = basis;

		exponents = new float[(int)octaves+1];
		float frequency = 1.0f;
		for (int i = 0; i <= (int)octaves; i++) {
			exponents[i] = (float)Math.pow(frequency, -H);
			frequency *= lacunarity;
		}
	}

	public void setBasis(Function2D basis) {
		this.basis = basis;
	}

	public Function2D getBasisType() {
		return basis;
	}

	public float evaluate(float x, float y) {
		float value = 0.0f;
		float remainder;
		int i;
		
		// to prevent "cascading" effects
		x += 371;
		y += 529;
		
		for (i = 0; i < (int)octaves; i++) {
			value += basis.evaluate(x, y) * exponents[i];
			x *= lacunarity;
			y *= lacunarity;
		}

		remainder = octaves - (int)octaves;
		if (remainder != 0)
			value += remainder * basis.evaluate(x, y) * exponents[i];

		return value;
	}

}
