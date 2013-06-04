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

import java.awt.image.*;
import java.util.*;

public class CellularFunction2D implements Function2D {

	public float distancePower = 2;
	public boolean cells = false;
	public boolean angular = false;
	private float[] coefficients = { 1, 0, 0, 0 };
	private Random random = new Random();
	private Point[] results = null;
	
	public CellularFunction2D() {
		results = new Point[2];
		for (int j = 0; j < results.length; j++)
			results[j] = new Point();
	}
	
	public void setCoefficient(int c, float v) {
		coefficients[c] = v;
	}
	
	public float getCoefficient(int c) {
		return coefficients[c];
	}
	
	class Point {
		int index;
		float x, y;
		float distance;
	}
	
	private float checkCube(float x, float y, int cubeX, int cubeY, Point[] results) {
		random.setSeed(571*cubeX + 23*cubeY);
		int numPoints = 3 + random.nextInt() % 4;
		numPoints = 4;

		for (int i = 0; i < numPoints; i++) {
			float px = random.nextFloat();
			float py = random.nextFloat();
			float dx = Math.abs(x-px);
			float dy = Math.abs(y-py);
			float d;
			if (distancePower == 1.0f)
				d = dx + dy;
			else if (distancePower == 2.0f)
				d = (float)Math.sqrt(dx*dx + dy*dy);
			else
				d = (float)Math.pow(Math.pow(dx, distancePower) + Math.pow(dy, distancePower), 1/distancePower);

			// Insertion sort
			for (int j = 0; j < results.length; j++) {
				if (results[j].distance == Double.POSITIVE_INFINITY) {
					Point last = results[j];
					last.distance = d;
					last.x = px;
					last.y = py;
					results[j] = last;
					break;
				} else if (d < results[j].distance) {
					Point last = results[results.length-1];
					for (int k = results.length-1; k > j; k--)
						results[k] = results[k-1];
					last.distance = d;
					last.x = px;
					last.y = py;
					results[j] = last;
					break;
				}
			}
		}
		return results[1].distance;
	}
	
	public float evaluate(float x, float y) {
		for (int j = 0; j < results.length; j++)
			results[j].distance = Float.POSITIVE_INFINITY;

		int ix = (int)x;
		int iy = (int)y;
		float fx = x-ix;
		float fy = y-iy;

		float d = checkCube(fx, fy, ix, iy, results);
		if (d > fy)
			d = checkCube(fx, fy+1, ix, iy-1, results);
		if (d > 1-fy)
			d = checkCube(fx, fy-1, ix, iy+1, results);
		if (d > fx) {
			checkCube(fx+1, fy, ix-1, iy, results);
			if (d > fy)
				d = checkCube(fx+1, fy+1, ix-1, iy-1, results);
			if (d > 1-fy)
				d = checkCube(fx+1, fy-1, ix-1, iy+1, results);
		}
		if (d > 1-fx) {
			d = checkCube(fx-1, fy, ix+1, iy, results);
			if (d > fy)
				d = checkCube(fx-1, fy+1, ix+1, iy-1, results);
			if (d > 1-fy)
				d = checkCube(fx-1, fy-1, ix+1, iy+1, results);
		}

		float t = 0;
		for (int i = 0; i < 2; i++)
			t += coefficients[i] * results[i].distance;
		if (angular)
			t += Math.atan2(fy-results[0].y, fx-results[0].x) / (2*Math.PI) + 0.5;
		return t;
	}
	
}
