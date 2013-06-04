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
public class Matrix4f {
	public float m00, m01, m02, m03;
	public float m10, m11, m12, m13;
	public float m20, m21, m22, m23;
	public float m30, m31, m32, m33;

	public Matrix4f() {
		setIdentity();
	}

	public Matrix4f( Matrix4f m ) {
		set( m );
	}

	public Matrix4f(float[] m) {
		set( m );
	}

	public void set( Matrix4f m) {
		m00 = m.m00;
		m01 = m.m01;
		m02 = m.m02;
		m03 = m.m03;
		m10 = m.m10;
		m11 = m.m11;
		m12 = m.m12;
		m13 = m.m13;
		m20 = m.m20;
		m21 = m.m21;
		m22 = m.m22;
		m23 = m.m23;
		m30 = m.m30;
		m31 = m.m31;
		m32 = m.m32;
		m33 = m.m33;
	}

	public void set(float[] m) {
		m00 = m[0];
		m01 = m[1];
		m02 = m[2];
		m03 = m[3];
		m10 = m[4];
		m11 = m[5];
		m12 = m[6];
		m13 = m[7];
		m20 = m[8];
		m21 = m[9];
		m22 = m[10];
		m23 = m[11];
		m30 = m[12];
		m31 = m[13];
		m32 = m[14];
		m33 = m[15];
	}

	public void get( Matrix4f m) {
		m.m00 = m00;
		m.m01 = m01;
		m.m02 = m02;
		m.m03 = m03;
		m.m10 = m10;
		m.m11 = m11;
		m.m12 = m12;
		m.m13 = m13;
		m.m20 = m20;
		m.m21 = m21;
		m.m22 = m22;
		m.m23 = m23;
		m.m30 = m30;
		m.m31 = m31;
		m.m32 = m32;
		m.m33 = m33;
	}

	public void get(float[] m) {
		m[0] = m00;
		m[1] = m01;
		m[2] = m02;
		m[3] = m03;
		m[4] = m10;
		m[5] = m11;
		m[6] = m12;
		m[7] = m13;
		m[8] = m20;
		m[9] = m21;
		m[10] = m22;
		m[11] = m23;
		m[12] = m30;
		m[13] = m31;
		m[14] = m32;
		m[15] = m33;
	}

	public void setIdentity() {
		m00 = 1.0f;
		m01 = 0.0f;
		m02 = 0.0f;
		m03 = 0.0f;

		m10 = 0.0f;
		m11 = 1.0f;
		m12 = 0.0f;
		m13 = 0.0f;

		m20 = 0.0f;
		m21 = 0.0f;
		m22 = 1.0f;
		m23 = 0.0f;

		m30 = 0.0f;
		m31 = 0.0f;
		m32 = 0.0f;
		m33 = 1.0f;
	}

	public void mul( Matrix4f m ) {
		float tm00 = m00;
		float tm01 = m01;
		float tm02 = m02;
		float tm03 = m03;
		float tm10 = m10;
		float tm11 = m11;
		float tm12 = m12;
		float tm13 = m13;
		float tm20 = m20;
		float tm21 = m21;
		float tm22 = m22;
		float tm23 = m23;
		float tm30 = m30;
		float tm31 = m31;
		float tm32 = m32;
		float tm33 = m33;

		m00 = tm00*m.m00 + tm10*m.m01 + tm20*m.m02 + tm30*m.m03;
		m01 = tm01*m.m00 + tm11*m.m01 + tm21*m.m02 + tm31*m.m03;
		m02 = tm02*m.m00 + tm12*m.m01 + tm22*m.m02 + tm32*m.m03;
		m03 = tm03*m.m00 + tm13*m.m01 + tm23*m.m02 + tm33*m.m03;
		m10 = tm00*m.m10 + tm10*m.m11 + tm20*m.m12 + tm30*m.m13;
		m11 = tm01*m.m10 + tm11*m.m11 + tm21*m.m12 + tm31*m.m13;
		m12 = tm02*m.m10 + tm12*m.m11 + tm22*m.m12 + tm32*m.m13;
		m13 = tm03*m.m10 + tm13*m.m11 + tm23*m.m12 + tm33*m.m13;
		m20 = tm00*m.m20 + tm10*m.m21 + tm20*m.m22 + tm30*m.m23;
		m21 = tm01*m.m20 + tm11*m.m21 + tm21*m.m22 + tm31*m.m23;
		m22 = tm02*m.m20 + tm12*m.m21 + tm22*m.m22 + tm32*m.m23;
		m23 = tm03*m.m20 + tm13*m.m21 + tm23*m.m22 + tm33*m.m23;
		m30 = tm00*m.m30 + tm10*m.m31 + tm20*m.m32 + tm30*m.m33;
		m31 = tm01*m.m30 + tm11*m.m31 + tm21*m.m32 + tm31*m.m33;
		m32 = tm02*m.m30 + tm12*m.m31 + tm22*m.m32 + tm32*m.m33;
		m33 = tm03*m.m30 + tm13*m.m31 + tm23*m.m32 + tm33*m.m33;
	}

	public void invert() {
		Matrix4f t = new Matrix4f( this );
		invert( t );
	}

	public void invert( Matrix4f t ) {
		m00 = t.m00;
		m01 = t.m10;
		m02 = t.m20;
		m03 = t.m03;

		m10 = t.m01;
		m11 = t.m11;
		m12 = t.m21;
		m13 = t.m13;

		m20 = t.m02;
		m21 = t.m12;
		m22 = t.m22;
		m23 = t.m23;

		m30 *= -1.0f;
		m31 *= -1.0f;
		m32 *= -1.0f;
		m33 = t.m33;
	}

	public void set( AxisAngle4f a ) {
		float halfTheta = a.angle * 0.5f;
		float cosHalfTheta = (float)Math.cos(halfTheta);
		float sinHalfTheta = (float)Math.sin(halfTheta);
		set( new Quat4f( a.x * sinHalfTheta, a.y * sinHalfTheta, a.z * sinHalfTheta, cosHalfTheta ) );
	}

	public void set( Quat4f q ) {
		float wx, wy, wz, xx, yy, yz, xy, xz, zz, x2, y2, z2;
		x2 = q.x + q.x;
		y2 = q.y + q.y;
		z2 = q.z + q.z;
		xx = q.x * x2;
		xy = q.x * y2;
		xz = q.x * z2;
		yy = q.y * y2;
		yz = q.y * z2;
		zz = q.z * z2;
		wx = q.w * x2;
		wy = q.w * y2;
		wz = q.w * z2;
		m00 = 1.0f - (yy + zz);
		m01 = xy - wz;
		m02 = xz + wy;
		m03 = 0.0f;
		m10 = xy + wz;
		m11 = 1.0f - (xx + zz);
		m12 = yz - wx;
		m13 = 0.0f;
		m20 = xz - wy;
		m21 = yz + wx;
		m22 = 1.0f - (xx + yy);
		m23 = 0.0f;
		m30 = 0;
		m31 = 0;
		m32 = 0;
		m33 = 1;
	}

	public void transform( Point3f v ) {
		float x = v.x * m00 + v.y * m10 + v.z * m20 + m30;
		float y = v.x * m01 + v.y * m11 + v.z * m21 + m31;
		float z = v.x * m02 + v.y * m12 + v.z * m22 + m32;
		v.x = x;
		v.y = y;
		v.z = z;
	}

	public void transform( Vector3f v ) {
		float x = v.x * m00 + v.y * m10 + v.z * m20;
		float y = v.x * m01 + v.y * m11 + v.z * m21;
		float z = v.x * m02 + v.y * m12 + v.z * m22;
		v.x = x;
		v.y = y;
		v.z = z;
	}

	public void setTranslation( Vector3f v ) {
		m30 = v.x;
		m31 = v.y;
		m32 = v.z;
	}

	public void set( float scale ) {
		m00 = scale;
		m11 = scale;
		m22 = scale;
	}

	public void rotX( float angle ) {
		float s = (float)Math.sin( angle );
		float c = (float)Math.cos( angle );
		m00 = 1.0f;
		m01 = 0.0f;
		m02 = 0.0f;
		m03 = 0.0f;

		m10 = 0.0f;
		m11 = c;
		m12 = s;
		m13 = 0.0f;

		m20 = 0.0f;
		m21 = -s;
		m22 = c;
		m23 = 0.0f;

		m30 = 0.0f;
		m31 = 0.0f;
		m32 = 0.0f;
		m33 = 1.0f;
	}

	public void rotY( float angle ) {
		float s = (float)Math.sin( angle );
		float c = (float)Math.cos( angle );
		m00 = c;
		m01 = 0.0f;
		m02 = -s;
		m03 = 0.0f;

		m10 = 0.0f;
		m11 = 1.0f;
		m12 = 0.0f;
		m13 = 0.0f;

		m20 = s;
		m21 = 0.0f;
		m22 = c;
		m23 = 0.0f;

		m30 = 0.0f;
		m31 = 0.0f;
		m32 = 0.0f;
		m33 = 1.0f;
	}

	public void rotZ( float angle ) {
		float s = (float)Math.sin( angle );
		float c = (float)Math.cos( angle );
		m00 = c;
		m01 = s;
		m02 = 0.0f;
		m03 = 0.0f;

		m10 = -s;
		m11 = c;
		m12 = 0.0f;
		m13 = 0.0f;

		m20 = 0.0f;
		m21 = 0.0f;
		m22 = 1.0f;
		m23 = 0.0f;

		m30 = 0.0f;
		m31 = 0.0f;
		m32 = 0.0f;
		m33 = 1.0f;
	}
/*
	void rotate(float angle, float x, float y, float z) {
		Matrix4f m = new Matrix4f();//FIXME
		m.MatrixFromAxisAngle(Vector3f(x, y, z), angle);
		Multiply(m);
	}
*/
}
