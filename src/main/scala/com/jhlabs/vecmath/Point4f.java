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

package com.jhlabs.vecmath;

/**
 * Vector math package, converted to look similar to javax.vecmath.
 */
public class Point4f extends Tuple4f {

	public Point4f() {
		this( 0, 0, 0, 0 );
	}
	
	public Point4f( float[] x ) {
		this.x = x[0];
		this.y = x[1];
		this.z = x[2];
		this.w = x[3];
	}

	public Point4f( float x, float y, float z, float w ) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Point4f( Point4f t ) {
		this.x = t.x;
		this.y = t.y;
		this.z = t.z;
		this.w = t.w;
	}

	public Point4f( Tuple4f t ) {
		this.x = t.x;
		this.y = t.y;
		this.z = t.z;
		this.w = t.w;
	}

	public float distanceL1( Point4f p ) {
		return Math.abs(x-p.x) + Math.abs(y-p.y) + Math.abs(z-p.z) + Math.abs(w-p.w);
	}

	public float distanceSquared( Point4f p ) {
		float dx = x-p.x;
		float dy = y-p.y;
		float dz = z-p.z;
		float dw = w-p.w;
		return dx*dx+dy*dy+dz*dz+dw*dw;
	}

	public float distance( Point4f p ) {
		float dx = x-p.x;
		float dy = y-p.y;
		float dz = z-p.z;
		float dw = w-p.w;
		return (float)Math.sqrt( dx*dx+dy*dy+dz*dz+dw*dw );
	}

}
