/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Used to combine preference scores; the class of operators that includes min(), max(), average(),
 * etc.
 * 
 * See http://en.wikipedia.org/wiki/Ordered_Weighted_Averaging_(OWA)_Aggregation_Operators
 * 
 * TODO: add attitudinal character and dispersion.
 * 
 * @author joel
 * 
 */
public abstract class OWAOperator {

	public OWAOperator() {
		// foo
	}

	/**
	 * Return the OWA F() value for the input list. If any of the input values is null, return null.
	 * 
	 * Maybe this should just be double[] rather than List<Double>.
	 */
	public Double F(List<Double> inputs) {
		if (inputs.size() == 0) {
			return null;
		}

		double[] weights = W(inputs.size());

		// Make a copy of the input that we can screw with
		List<Double> mutableInputs = new ArrayList<Double>(inputs);

		// Sort descending.
		Collections.sort(mutableInputs);
		Collections.reverse(mutableInputs);

		// Take the product.

		double result = 0.0;

		for (int i = 0; i < mutableInputs.size(); ++i) {
			Double v = mutableInputs.get(i);
			if (v == null) {
				return null;
			}
			result += v.doubleValue() * weights[i];
		}
		return new Double(result);
	}

	/**
	 * Return the list of weights for this operator, for this many inputs.
	 * 
	 * @return
	 */
	protected abstract double[] W(int size);

}
