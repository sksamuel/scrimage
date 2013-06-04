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
public class Quat4f extends Tuple4f {

	public Quat4f() {
		this( 0, 0, 0, 0 );
	}
	
	public Quat4f( float[] x ) {
		this.x = x[0];
		this.y = x[1];
		this.z = x[2];
		this.w = x[3];
	}

	public Quat4f( float x, float y, float z, float w ) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Quat4f( Quat4f t ) {
		this.x = t.x;
		this.y = t.y;
		this.z = t.z;
		this.w = t.w;
	}

	public Quat4f( Tuple4f t ) {
		this.x = t.x;
		this.y = t.y;
		this.z = t.z;
		this.w = t.w;
	}

	public void set( AxisAngle4f a ) {
		float halfTheta = a.angle * 0.5f;
		float cosHalfTheta = (float)Math.cos(halfTheta);
		float sinHalfTheta = (float)Math.sin(halfTheta);
		x = a.x * sinHalfTheta;
		y = a.y * sinHalfTheta;
		z = a.z * sinHalfTheta;
		w = cosHalfTheta;
	}

/*
	public void EulerToQuaternion(float roll, float pitch, float yaw)
	{
		float cr, cp, cy, sr, sp, sy, cpcy, spsy;
		cr = cos(roll/2);
		cp = cos(pitch/2);
		cy = cos(yaw/2);
		sr = sin(roll/2);
		sp = sin(pitch/2);
		sy = sin(yaw/2);
		cpcy = cp * cy;
		spsy = sp * sy;
		w = cr * cpcy + sr * spsy;
		x = sr * cpcy - cr * spsy;
		y = cr * sp * cy + sr * cp * sy;
		z = cr * cp * sy - sr * sp * cy;
	}
*/

	public void normalize() {
		float d = 1.0f/( x*x+y*y+z*z+w*w );
		x *= d;
		y *= d;
		z *= d;
		w *= d;
	}

/*
	public void mul( Quat4f q ) {
		Quat4f q3 = new Quat4f();
		Vector3f vectorq1 = new Vector3f( x, y, z );
		Vector3f vectorq2 = new Vector3f( q.x, q.y, q.z );

		Vector3f tempvec1 = new Vector3f( vectorq1 );
		Vector3f tempvec2;
		Vector3f tempvec3;
		q3.w = (w*q.w) - tempvec1.dot(vectorq2);
		tempvec1.cross(vectorq2);
		tempvec2.x = w * q.x;
		tempvec2.y = w * q.y;
		tempvec2.z = w * q.z;
		tempvec3.x = q.w * x;
		tempvec3.y = q.w * y;
		tempvec3.z = q.w * z;
		q3.x = tempvec1.x + tempvec2.x + tempvec3.x;
		q3.y = tempvec1.y + tempvec2.y + tempvec3.y;
		q3.z = tempvec1.z + tempvec2.z + tempvec3.z;
		set(q3);
	}
*/

	public void set( Matrix4f m ) {
		float s;
		int i;

		float tr = m.m00 + m.m11 + m.m22;

		if (tr > 0.0) {
			s = (float)Math.sqrt(tr + 1.0f);
			w = s / 2.0f;
			s = 0.5f / s;
			x = (m.m12 - m.m21) * s;
			y = (m.m20 - m.m02) * s;
			z = (m.m01 - m.m10) * s;
		} else {		
			i = 0;
			if ( m.m11 > m.m00 ) {
				i = 1;
				if ( m.m22 > m.m11 ) {
					i = 2;
				} else {
				}
			} else {
				if ( m.m22 > m.m00 ) {
					i = 2;
				} else {
				}
			}

			switch ( i ) {
			case 0:
				s = (float)Math.sqrt ((m.m00 - (m.m11 + m.m22)) + 1.0f);
				x = s * 0.5f;
				if (s != 0.0)
					s = 0.5f / s;
				w = (m.m12 - m.m21) * s;
				y = (m.m01 + m.m10) * s;
				z = (m.m02 + m.m20) * s;
				break;
			case 1:
				s = (float)Math.sqrt ((m.m11 - (m.m22 + m.m00)) + 1.0f);
				y = s * 0.5f;
				if (s != 0.0)
					s = 0.5f / s;
				w = (m.m20 - m.m02) * s;
				z = (m.m12 + m.m21) * s;
				x = (m.m10 + m.m01) * s;
				break;
			case 2:
				s = (float)Math.sqrt ((m.m00 - (m.m11 + m.m22)) + 1.0f);
				z = s * 0.5f;
				if (s != 0.0)
					s = 0.5f / s;
				w = (m.m01 - m.m10) * s;
				x = (m.m20 + m.m02) * s;
				y = (m.m21 + m.m12) * s;
				break;
			}

		}
	}

}
