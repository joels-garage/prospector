/*
 * Copyright 2008 Joel Truher
 * Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import java.util.Arrays;

/**
 * @author joel
 */
public class MinOWAOperator extends OWAOperator {

	public MinOWAOperator() {
		// foo
	}

	/**
	 * The min weights are all zero except for the last one.
	 */
	@Override
	protected double[] W(int size) {
		double[] result = new double[size];
		Arrays.fill(result, 0.0);
		result[size - 1] = 1.0;
		return result;
	}

}
