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

public class MathFunction1D implements Function1D {

	public final static int SIN = 1;
	public final static int COS = 2;
	public final static int TAN = 3;
	public final static int SQRT = 4;
	public final static int ASIN = -1;
	public final static int ACOS = -2;
	public final static int ATAN = -3;
	public final static int SQR = -4;

	private int operation;
	
	public MathFunction1D(int operation) {
		this.operation = operation;
	}
	
	public float evaluate(float v) {
		switch (operation) {
		case SIN:
			return (float)Math.sin(v);
		case COS:
			return (float)Math.cos(v);
		case TAN:
			return (float)Math.tan(v);
		case SQRT:
			return (float)Math.sqrt(v);
		case ASIN:
			return (float)Math.asin(v);
		case ACOS:
			return (float)Math.acos(v);
		case ATAN:
			return (float)Math.atan(v);
		case SQR:
			return v*v;
		}
		return v;
	}
}

