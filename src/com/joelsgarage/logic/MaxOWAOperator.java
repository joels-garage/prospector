/*
 * Copyright 2008 Joel Truher
 * Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import java.util.Arrays;

/**
 * @author joel
 */
public class MaxOWAOperator extends OWAOperator {

	public MaxOWAOperator() {
		// foo
	}

	/**
	 * The max weights are all zero except for the first one.
	 */
	@Override
	protected double[] W(int size) {
		double[] result = new double[size];
		Arrays.fill(result, 0.0);
		result[0] = 1.0;
		return result;
	}

}
