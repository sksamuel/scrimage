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

package thirdparty.jhlabs.math;

import java.util.*;

/**
 * Perlin Noise functions
 */
public class Noise implements Function1D, Function2D, Function3D {

   private static final Random randomGenerator = new Random();

   public float evaluate(float x) {
      return noise1(x);
   }

   public float evaluate(float x, float y) {
      return noise2(x, y);
   }

   public float evaluate(float x, float y, float z) {
      return noise3(x, y, z);
   }

   private final static int B = 0x100;
   private final static int BM = 0xff;
   private final static int N = 0x1000;

   static int[] p = new int[B + B + 2];
   static float[][] g3 = new float[B + B + 2][3];
   static float[][] g2 = new float[B + B + 2][2];
   static float[] g1 = new float[B + B + 2];
   static boolean start = true;

   private static float sCurve(float t) {
      return t * t * (3.0f - 2.0f * t);
   }

   /**
    * Compute 1-dimensional Perlin noise.
    *
    * @param x the x value
    * @return noise value at x in the range -1..1
    */
   public static float noise1(float x) {
      int bx0, bx1;
      float rx0, rx1, sx, t, u, v;

      if (start) {
         start = false;
         init();
      }

      t = x + N;
      bx0 = ((int) t) & BM;
      bx1 = (bx0 + 1) & BM;
      rx0 = t - (int) t;
      rx1 = rx0 - 1.0f;

      sx = sCurve(rx0);

      u = rx0 * g1[p[bx0]];
      v = rx1 * g1[p[bx1]];
      return 2.3f * lerp(sx, u, v);
   }

   /**
    * Compute 2-dimensional Perlin noise.
    *
    * @param x the x coordinate
    * @param y the y coordinate
    * @return noise value at (x,y)
    */
   public static float noise2(float x, float y) {
      int bx0, bx1, by0, by1, b00, b10, b01, b11;
      float rx0;
      float rx1;
      float ry0;
      float ry1;
      float[] q;
      float sx;
      float sy;
      float a;
      float b;
      float t;
      float u;
      float v;
      int i, j;

      if (start) {
         start = false;
         init();
      }

      t = x + N;
      bx0 = ((int) t) & BM;
      bx1 = (bx0 + 1) & BM;
      rx0 = t - (int) t;
      rx1 = rx0 - 1.0f;

      t = y + N;
      by0 = ((int) t) & BM;
      by1 = (by0 + 1) & BM;
      ry0 = t - (int) t;
      ry1 = ry0 - 1.0f;

      i = p[bx0];
      j = p[bx1];

      b00 = p[i + by0];
      b10 = p[j + by0];
      b01 = p[i + by1];
      b11 = p[j + by1];

      sx = sCurve(rx0);
      sy = sCurve(ry0);

      q = g2[b00];
      u = rx0 * q[0] + ry0 * q[1];
      q = g2[b10];
      v = rx1 * q[0] + ry0 * q[1];
      a = lerp(sx, u, v);

      q = g2[b01];
      u = rx0 * q[0] + ry1 * q[1];
      q = g2[b11];
      v = rx1 * q[0] + ry1 * q[1];
      b = lerp(sx, u, v);

      return 1.5f * lerp(sy, a, b);
   }

   /**
    * Compute 3-dimensional Perlin noise.
    *
    * @param x the x coordinate
    * @param y the y coordinate
    * @return noise value at (x,y,z)
    */
   public static float noise3(float x, float y, float z) {
      int bx0, bx1, by0, by1, bz0, bz1, b00, b10, b01, b11;
      float rx0;
      float rx1;
      float ry0;
      float ry1;
      float rz0;
      float rz1;
      float[] q;
      float sy;
      float sz;
      float a;
      float b;
      float c;
      float d;
      float t;
      float u;
      float v;
      int i, j;

      if (start) {
         start = false;
         init();
      }

      t = x + N;
      bx0 = ((int) t) & BM;
      bx1 = (bx0 + 1) & BM;
      rx0 = t - (int) t;
      rx1 = rx0 - 1.0f;

      t = y + N;
      by0 = ((int) t) & BM;
      by1 = (by0 + 1) & BM;
      ry0 = t - (int) t;
      ry1 = ry0 - 1.0f;

      t = z + N;
      bz0 = ((int) t) & BM;
      bz1 = (bz0 + 1) & BM;
      rz0 = t - (int) t;
      rz1 = rz0 - 1.0f;

      i = p[bx0];
      j = p[bx1];

      b00 = p[i + by0];
      b10 = p[j + by0];
      b01 = p[i + by1];
      b11 = p[j + by1];

      t = sCurve(rx0);
      sy = sCurve(ry0);
      sz = sCurve(rz0);

      q = g3[b00 + bz0];
      u = rx0 * q[0] + ry0 * q[1] + rz0 * q[2];
      q = g3[b10 + bz0];
      v = rx1 * q[0] + ry0 * q[1] + rz0 * q[2];
      a = lerp(t, u, v);

      q = g3[b01 + bz0];
      u = rx0 * q[0] + ry1 * q[1] + rz0 * q[2];
      q = g3[b11 + bz0];
      v = rx1 * q[0] + ry1 * q[1] + rz0 * q[2];
      b = lerp(t, u, v);

      c = lerp(sy, a, b);

      q = g3[b00 + bz1];
      u = rx0 * q[0] + ry0 * q[1] + rz1 * q[2];
      q = g3[b10 + bz1];
      v = rx1 * q[0] + ry0 * q[1] + rz1 * q[2];
      a = lerp(t, u, v);

      q = g3[b01 + bz1];
      u = rx0 * q[0] + ry1 * q[1] + rz1 * q[2];
      q = g3[b11 + bz1];
      v = rx1 * q[0] + ry1 * q[1] + rz1 * q[2];
      b = lerp(t, u, v);

      d = lerp(sy, a, b);

      return 1.5f * lerp(sz, c, d);
   }

   public static float lerp(float t, float a, float b) {
      return a + t * (b - a);
   }

   private static void normalize2(float[] v) {
      float s = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1]);
      v[0] = v[0] / s;
      v[1] = v[1] / s;
   }

   static void normalize3(float[] v) {
      float s = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
      v[0] = v[0] / s;
      v[1] = v[1] / s;
      v[2] = v[2] / s;
   }

   private static int random() {
      return randomGenerator.nextInt() & 0x7fffffff;
   }

   private static void init() {
      int i, j, k;

      for (i = 0; i < B; i++) {
         p[i] = i;

         g1[i] = (float) ((random() % (B + B)) - B) / B;

         for (j = 0; j < 2; j++)
            g2[i][j] = (float) ((random() % (B + B)) - B) / B;
         normalize2(g2[i]);

         for (j = 0; j < 3; j++)
            g3[i][j] = (float) ((random() % (B + B)) - B) / B;
         normalize3(g3[i]);
      }

      for (i = B - 1; i >= 0; i--) {
         k = p[i];
         p[i] = p[j = random() % B];
         p[j] = k;
      }

      for (i = 0; i < B + 2; i++) {
         p[B + i] = p[i];
         g1[B + i] = g1[i];
         for (j = 0; j < 2; j++)
            g2[B + i][j] = g2[i][j];
         for (j = 0; j < 3; j++)
            g3[B + i][j] = g3[i][j];
      }
   }

}
