/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import java.util.Arrays;

/**
 * @author joel
 */
public class AverageOWAOperator extends OWAOperator {
	public AverageOWAOperator() {
		// foo
	}

	/**
	 * The average weights are all the same, equal to 1/size.
	 */
	@Override
	protected double[] W(int size) {
		double averageWeight = (double) 1 / size;
		double[] result = new double[size];
		Arrays.fill(result, averageWeight);
		return result;
	}
}
