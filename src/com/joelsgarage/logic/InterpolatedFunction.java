/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

/**
 * Extremely quick and dirty interpolator for R2[0,1]=>R[0,1], with a grid of control points.
 * 
 * @author joel
 * 
 */
public class InterpolatedFunction {
	/**
	 * the array of values. There are 11 rows and 11 columns.
	 */
	private double[][] values;

	public InterpolatedFunction(double[][] values) {
		// make sure this is a legal initialization
		int rows = values.length;
		if (rows != 11) {
			// barf
			return;
		}
		for (int i = 0; i < rows; ++i) {
			int columns = values[i].length;
			if (columns != 11) {
				// barf
				return;
			}
		}
		this.values = values;
	}

	/**
	 * The value of the function.
	 * 
	 * @param x
	 *            [0,1]
	 * @param y
	 *            [0,1]
	 * @return [0,1]
	 */
	double f(double x, double y) {
		int xIndexLow = (int) Math.floor(x * 10);
		int xIndexHigh = xIndexLow + 1;
		int yIndexLow = (int) Math.floor(y * 10);
		int yIndexHigh = yIndexLow + 1;

		// how far between the indices, from 0 to 1
		double xOffset = 0.0;
		if (xIndexLow >= 10) { // off the end => you get the edge
			xIndexLow = 9;
			xIndexHigh = 10;
			xOffset = 1.0;
		} else if (xIndexHigh <= 0) {
			xIndexLow = 0;
			xIndexHigh = 1;
			xOffset = 0.0;
		} else {
			xOffset = x * 10 - xIndexLow;
		}
		double yOffset = 0.0;
		if (yIndexLow >= 10) {
			yIndexLow = 9;
			yIndexHigh = 10;
			yOffset = 1.0;
		} else if (yIndexHigh <= 0) {
			yIndexLow = 0;
			yIndexHigh = 1;
			yOffset = 0.0;
		} else {
			yOffset = y * 10 - yIndexLow;
		}

		if (xIndexLow < 0 || yIndexLow < 0 || xIndexHigh > 10 || yIndexHigh > 10) {
			return -1.0;
		}

		double lowLow = this.values[xIndexLow][yIndexLow];
		double highLow = this.values[xIndexHigh][yIndexLow];
		double lowHigh = this.values[xIndexLow][yIndexHigh];
		double highHigh = this.values[xIndexHigh][yIndexHigh];

		// bilinear interpolation

		double xForLowY = lowLow + (highLow - lowLow) * xOffset;
		double xForHighY = lowHigh + (highHigh - lowHigh) * xOffset;
		double result = xForLowY + (xForHighY - xForLowY) * yOffset;

		return result;
	}
}
